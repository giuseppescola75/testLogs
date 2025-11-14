package com.juliusbaer.cmt.pat.preClearance.validatePreClearance;

import com.juliusbaer.cmt.pat.app.activoin.openapi.api.DefaultApi;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.ValidatePreClearanceRequest;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.ValidatePreClearanceResponse;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DefaultPreClearanceService {
    private static final Logger SVL = Loggers.SVL;
    private static final Logger ATL = Loggers.ATL;

    private final DefaultApi preClearanceApi;

    @Autowired
    public DefaultPreClearanceService(@Qualifier("preClearanceDefaultApi") DefaultApi preClearanceApi) {
        this.preClearanceApi = preClearanceApi;
    }

    public ValidatePreClearanceResponse getValidatePreClearance(ValidatePreClearanceRequest request) {
        SVL.info("status=REQUESTED");
        ATL.info("status=SUCCESS");
        return this.preClearanceApi.validatePreClearance(request);
    }
}
