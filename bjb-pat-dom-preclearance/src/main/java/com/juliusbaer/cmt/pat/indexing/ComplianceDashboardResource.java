package com.juliusbaer.cmt.pat.indexing;

import static com.juliusbaer.cmt.pat.common.AuthoritiesConstants.ADMIN;
import static com.juliusbaer.cmt.pat.common.AuthoritiesConstants.FLOWABLE_ADMIN;
import static com.juliusbaer.cmt.pat.common.AuthoritiesConstants.USER;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.flowable.common.rest.api.DataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowable.core.spring.security.SecurityUtils;
import com.flowable.indexing.utils.ElasticsearchResultConverter;
import com.flowable.platform.common.Page;
import com.flowable.platform.rest.service.api.util.ResourceUtils;

@RestController
public class ComplianceDashboardResource {

    private final ComplianceDashboardService complianceDashboardService;
    private final ComplianceDashboardResultMapper resultMapper;

    public ComplianceDashboardResource(ComplianceDashboardService complianceDashboardService, ComplianceDashboardResultMapper resultMapper) {
        this.complianceDashboardService = complianceDashboardService;
        this.resultMapper = resultMapper;
    }

    @GetMapping( "/compliance-dashboard")
    public ResponseEntity<DataResponse<ComplianceDashboardCaseInstanceRepresentation>> queryCaseInstances(
            @RequestParam(required = false, defaultValue = "", value= "requesters") String requesters,
            @RequestParam(required = false, defaultValue = "", value= "statuses") String statuses,
            @RequestParam(required = false, defaultValue = "", value= "locations") String locations,
            @RequestParam(required = false, defaultValue = "0", value= "start") int start,
            @RequestParam(required = false, defaultValue = "10", value= "size") int size
            ) {

        if (!hasUserGrantedAuthorities(Arrays.asList(USER, FLOWABLE_ADMIN, ADMIN))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have correct group");
        }

        List<String> requestersList = getList(requesters);

        List<String> statusList = getList(statuses);

        List<String> locationList = getList(locations);

        JsonNode result = complianceDashboardService.queryCaseInstances(requestersList, statusList, locationList, start, size);

        Page<ComplianceDashboardCaseInstanceRepresentation> page = ElasticsearchResultConverter.convertSearchResult(result, resultMapper, start, size);
        return ResponseEntity.ok(ResourceUtils.setDataResponse(page));
    }

    private static List<String> getList(String requesters) {
        return Arrays.stream(requesters.split(","))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private boolean hasUserGrantedAuthorities(List<String> allowedAuthorities) {
        return !(allowedAuthorities.stream()
                .distinct()
                .filter(SecurityUtils.getCurrentUserSecurityScope().getGroupKeys()::contains)
                .collect(Collectors.toSet()).isEmpty());
    }

}
