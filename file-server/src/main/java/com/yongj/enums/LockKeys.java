package com.yongj.enums;

import java.util.function.Function;

/**
 * @author yongj.zhuang
 */
public final class LockKeys {
    private LockKeys() {

    }

    /**
     * Supplier of lock key for change to file_sharing
     * <p>
     * Only owner of the file change this access to the file, so we just lock the file.
     */
    public static final Function<String /* fileKey */, String /* lockKey */> fileAccessKeySup = (fileUuid) -> "file:user:access:" + fileUuid;

    /**
     * Supplier of lock key for file
     */
    public static final Function<String /* fileKey */, String /* lockKey */> fileKeySup = (fileUuid) -> "file:uuid:" + fileUuid;
}
