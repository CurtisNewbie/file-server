package com.yongj.services;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Service for managing temporary token shared for file downloading
 *
 * @author yongjie.zhuang
 */
@Validated
public interface TempTokenFileDownloadService {

    /**
     * Generate a one-time used token for file sharing
     *
     * @param uuid uuid
     * @return token
     */
    String generateTempTokenForFile(@NotEmpty String uuid) throws MsgEmbeddedException;

    /**
     * Get uuid for the given token
     *
     * @param token token return uuid
     */
    String getUuidByToken(@NotEmpty String token);

    /**
     * Remove token
     *
     * @param token token
     */
    void removeToken(@NotEmpty String token);
}
