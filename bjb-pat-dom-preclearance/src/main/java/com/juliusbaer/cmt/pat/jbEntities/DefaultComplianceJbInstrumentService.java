package com.juliusbaer.cmt.pat.jbEntities;

import com.juliusbaer.cmt.pat.app.compliancejb.openapi.api.DefaultApi;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.ValidateComplianceJbInstrumentRequest;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.ValidateComplianceJbInstrumentResponse;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class DefaultComplianceJbInstrumentService {

    private final DefaultApi jbEntitiesDefaultApi;

    private static final Logger SVL = Loggers.SVL;
    private static final Logger ATL = Loggers.ATL;

    @Autowired
    public DefaultComplianceJbInstrumentService(@Qualifier("jbEntitiesDefaultApi") DefaultApi jbEntitiesDefaultApi) {
        this.jbEntitiesDefaultApi = jbEntitiesDefaultApi;
    }

    public ValidateComplianceJbInstrumentResponse getValidateComplianceJbInstrument(ValidateComplianceJbInstrumentRequest request) {
        SVL.info("status=REQUESTED");
        ATL.info("status=SUCCESS");
        return this.jbEntitiesDefaultApi.validateComplianceJbInstrument(request);
    }
}