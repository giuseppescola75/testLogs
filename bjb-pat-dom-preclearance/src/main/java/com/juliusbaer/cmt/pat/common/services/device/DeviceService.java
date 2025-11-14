package com.juliusbaer.cmt.pat.common.services.device;

import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class DeviceService {
    private static final Logger ATL = Loggers.ATL;

    // A more comprehensive pattern to catch common mobile/tablet user agents.
    // This is still simple, but more robust than just 'mobile' or 'iphone'.
    private static final Pattern MOBILE_DEVICE_PATTERN = Pattern.compile(
            ".*(android|ipad|iphone|ipod|windows phone|mobile|iemobile).*",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Checks if the given User-Agent string corresponds to a mobile device.
     * @param userAgent The User-Agent string from the HTTP request header.
     * @return true if it's likely a mobile device, false otherwise.
     */
    public boolean isMobileDevice(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }

        ATL.info("userAgent: " + userAgent);
        // Check if the User-Agent matches the defined mobile pattern
        return MOBILE_DEVICE_PATTERN.matcher(userAgent).matches();
    }
}