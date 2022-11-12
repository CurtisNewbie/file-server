package com.yongj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;


/**
 * Configuration for event sync
 * <p>
 * For example:
 *
 * <pre>
 * event.sync:
 *  ip-address:
 *  enabled:
 *  secret:
 * </pre>
 *
 * @author yongj.zhuang
 */
@Data
@Configuration
public class EventSyncConfig {

    @Value("${event.sync.ip-address:}")
    private String[] ipAddress;

    @Value("${event.sync.secret:}")
    private String secret;

    @Value("${event.sync.enabled:false}")
    private boolean enabled;

    public boolean permitIpAddress(String ip) {
        if (ipAddress.length < 1) return true; // permit all
        for (String ipa : ipAddress) {
            if (Objects.equals(ipa, ip)) return true;
        }
        return false;
    }
}
