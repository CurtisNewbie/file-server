package com.yongj.vo;

import com.yongj.enums.TokenType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Request vo for generating temp token for file downloading
 *
 * @author yongjie.zhuang
 */
@Data
public class GenerateTokenReqVo {

    @NotNull(message = "file key is required")
    private String fileKey;

    /** Token Type */
    private TokenType tokenType = TokenType.DOWNLOAD;

}
