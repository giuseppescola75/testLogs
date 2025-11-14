package com.juliusbaer.cmt.pat.common.services.ldap.controller;


import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.idm.api.PlatformUser;
import com.juliusbaer.cmt.pat.common.services.ldap.PatDataWorker;
import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pat/exbemployee")
public class PatExBemployeeController {


    private static final Logger SVL = Loggers.SVL;
    private static final Logger BAL = Loggers.BAL;
    private final PlatformIdentityService platformIdentityService;

    public PatExBemployeeController(PlatformIdentityService platformIdentityService) {
        {
            this.platformIdentityService = platformIdentityService;
        }
    }

    @GetMapping("/fetchEmployees")
    public List<PatDataWorker> fetchEmployees(@RequestParam("search") String search) {
        SVL.info("endpoint=fetchEmployees,method=GET,action={},status={}", LogVocab.ActionSVL.REQUESTED.name(), LogVocab.StatusSVL.SUCCESS.name());

        List<PatDataWorker> patDataWorkers = platformIdentityService.createPlatformUserQuery().userDisplayNameLikeIgnoreCase("%" + search + "%").list().stream().map(user -> {
            return new PatDataWorker(user.getId(), user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getId(), resolveShortName(user));
        }).toList();

        BAL.info("eventType=fetchEmployees,objectType=Employee,action={},status={},recordCount={}", LogVocab.ActionBAL.CREATE.name(), LogVocab.StatusBAL.SUCCESS.name(), patDataWorkers.size());
        return patDataWorkers;
    }

    @GetMapping("/fetchEmployeesByGroup")
    public List<PatDataWorker> fetchEmployeesByGroup(@RequestParam("groupName") String groupName) {
        SVL.info("endpoint=fetchEmployeesByGroup,method=GET,action={},status={}", LogVocab.ActionSVL.REQUESTED.name(), LogVocab.StatusSVL.SUCCESS.name());
        List<PatDataWorker> patDataWorkers = platformIdentityService.createPlatformUserQuery().memberOfGroupKey(groupName).list().stream().map(user -> {
            return new PatDataWorker(user.getId(), user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getId(), resolveShortName(user));
        }).toList();

        BAL.info("eventType=fetchEmployeesByGroup,objectType=Employee,action={},status={},recordCount={}", LogVocab.ActionBAL.CREATE.name(), LogVocab.StatusBAL.SUCCESS.name(), patDataWorkers.size());
        return patDataWorkers;
    }

    private String resolveShortName(PlatformUser user) {
        String effectiveShortName = user.findString("shortName");
        if (effectiveShortName == null) {
            effectiveShortName = user.getId();
        }

        return mapShortName(effectiveShortName);
    }

    private String mapShortName(String name) {
        if (name == null || !name.contains("@")) {
            return name;
        }
        return name.split("@")[0];
    }

}
