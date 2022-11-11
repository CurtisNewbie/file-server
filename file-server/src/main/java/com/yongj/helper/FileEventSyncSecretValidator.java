package com.yongj.helper;

/**
 * Validator for secret used for file event syncing
 *
 * @author yongj.zhuang
 */
public interface FileEventSyncSecretValidator {

    /** validate secret */
    boolean validate(String secret);
}
