package com.juliusbaer.cmt.pat.indexing;

import static com.flowable.platform.service.index.IndexConstants.CASE_INSTANCES_INDEX;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.flowable.common.engine.api.FlowableIllegalStateException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.spring.security.SecurityUtils;
import com.flowable.indexing.SearchService;


@Service
public class ComplianceDashboardService {

    private static final String COMPLIANCE_DASHBOARD_QUERY_KEY = "pat-compliance-dashboard-case-query";
    private static final String PARAM_START = "start";
    private static final String PARAM_SIZE = "size";
    private static final String ERROR_MESSAGE_COULD_NOT_LOAD_QUERY = "Could not load query from template.";
    private static final String PARAM_CURRENT_USER_ID = "currentUserId";
    private static final String PARAM_REQUESTERS = "requesters";
    private static final String PARAM_LOCATIONS = "locations";
    private static final String PARAM_STATUSES = "statuses";
    private static final String PARAM_NOT_START_USER = "notStartUser";

    private final SearchService searchService;
    private final ObjectMapper objectMapper;
    private final PlatformIdentityService platformIdentityService;

    public ComplianceDashboardService(SearchService searchService, ObjectMapper objectMapper, PlatformIdentityService platformIdentityService) {
        this.searchService = searchService;
        this.objectMapper = objectMapper;
        this.platformIdentityService = platformIdentityService;
    }

    public JsonNode queryCaseInstances(List<String> requesters, List<String> statuses, List<String> locations, int start, int size) {
        Map<String, Object> params = createQueryParams(requesters, statuses, locations, start, size);

        String query = loadQuery(CASE_INSTANCES_INDEX, COMPLIANCE_DASHBOARD_QUERY_KEY, params);
        return searchService.query(CASE_INSTANCES_INDEX, query);
    }

    private Map<String, Object> createQueryParams(List<String> requesters, List<String> statuses, List<String> locations, int start, int size) {
        List<String> requestersFiltered = filterQueryStrings(requesters);
        List<String> statusesFiltered = filterQueryStrings(statuses);
        List<String> locationsFiltered = filterQueryStrings(locations);

        Map<String, Object> params = new HashMap<>();

        params.put(PARAM_REQUESTERS, requestersFiltered);
        params.put(PARAM_STATUSES, statusesFiltered);
        params.put(PARAM_LOCATIONS, locationsFiltered);
        params.put(PARAM_NOT_START_USER, SecurityUtils.getCurrentUserSecurityScope().getUserId());

        params.put(PARAM_START, start);
        params.put(PARAM_SIZE, size);
        params.put(PARAM_CURRENT_USER_ID, SecurityUtils.getCurrentUserSecurityScope().getUserId());

        if (locationsFiltered.isEmpty()) {
            List<String> comlianceLocations = platformIdentityService.createPlatformGroupQuery()
                    .groupMember(SecurityUtils.getCurrentUserSecurityScope().getUserId())
                    .list()
                    .stream()
                    .map(group -> group.getKey())
                    .filter(key -> key != null && key.startsWith("GA_PCL-PCR-LAA"))
                    .collect(Collectors.toList());
            params.put(PARAM_LOCATIONS, comlianceLocations);
        }

        return params;
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    private boolean isSet(Date date) {
        return date != null;
    }

    private boolean isSet(String s) {
        return s != null && hasContent(s.trim());
    }

    private List<String> filterQueryStrings(List<String> names) {
        return names.stream().map(String::trim).filter(this::hasContent).map(QueryParserBase::escape).collect(Collectors.toList());
    }

    private String loadQuery(String caseInstancesIndex, String complianceCaseSearchQueryKey, Map<String, Object> params) {
        try {
            ObjectNode query = searchService.resolveCustomQuery(caseInstancesIndex, complianceCaseSearchQueryKey, params);
            return objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            throw new FlowableIllegalStateException(ERROR_MESSAGE_COULD_NOT_LOAD_QUERY, e);
        }
    }

    private boolean hasContent(String s) {
        return !s.isEmpty();
    }
}