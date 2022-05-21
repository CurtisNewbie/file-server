package com.yongj.services;

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
     * @param id file's id
     * @param minutes duration of the token in minutes
     * @return token
     */
    String generateTempTokenForFile(int id, int minutes) ;

    /**
     * Get uuid for the given token
     *
     * @param token token return uuid
     */
    Integer getIdByToken(@NotEmpty String token);

    /**
     * Remove token
     *
     * @param token token
     */
    void removeToken(@NotEmpty String token);
}
