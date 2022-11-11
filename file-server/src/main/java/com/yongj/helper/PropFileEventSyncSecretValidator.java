package com.yongj.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Component
public class PropFileEventSyncSecretValidator implements FileEventSyncSecretValidator {

    public static final String FILE_EVENT_SYNC_SECRET_PROP = "event.sync.secret";

    private final String inMemorySecret = UUID.randomUUID().toString();
    private final Environment environment;

    public PropFileEventSyncSecretValidator(Environment environment) {
        this.environment = environment;
        if (!environment.containsProperty(FILE_EVENT_SYNC_SECRET_PROP)) {
            log.info("Missing secret for file event sync, random one is generated: '{}'", inMemorySecret);
        }
    }

    @Override
    public boolean validate(String secret) {
        final String expected = environment.getProperty(FILE_EVENT_SYNC_SECRET_PROP, inMemorySecret);
        return Objects.equals(secret, expected);
    }
}
