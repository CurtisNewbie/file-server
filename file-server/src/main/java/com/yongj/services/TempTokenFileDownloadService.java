package com.yongj.services;

import com.yongj.enums.TokenType;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Service for managing temporary token shared for file downloading
 *
 * @author yongjie.zhuang
 */
@Validated
public interface TempTokenFileDownloadService {

    /**
     * Extends the temp token's expiration, only TokenType.STREAMING is supported
     */
    void extendsStreamingTokenExp(@NotEmpty String token, int minutes);

    /**
     * Generate a temporary token for file access
     *
     * @param id      file's id
     * @param minutes duration of the token in minutes
     * @return token
     */
    String generateTempTokenForFile(int id, int minutes, @NotNull TokenType tokenType);

    /**
     * Generate a temporary token for file access
     *
     * @param id      file's id
     * @param minutes duration of the token in minutes
     * @return token
     */
    default String generateTempTokenForFile(int id, int minutes) {
        return generateTempTokenForFile(id, minutes, TokenType.DOWNLOAD);
    }

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
