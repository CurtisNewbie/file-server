package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.remote.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.exception.UserRegisteredException;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author yongjie.zhuang
 */
@ControllerAdvice
public class CtrlAdvice {

    private final Logger logger = LoggerFactory.getLogger(CtrlAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleGeneralException(Exception e) {
        logger.error("Exception occurred", e);
        return Result.error("Internal Error");
    }

    // TODO This doesn't really wrap the exception with a custom response
    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    @ResponseBody
    public Result<?> handleSizeLimitExceededException(Exception e) {
        logger.warn("Size limit exceeded - '{}'", e.getMessage());
        return Result.error("Size limit exceeded");
    }

    @ExceptionHandler({IllegalExtException.class, IllegalPathException.class})
    @ResponseBody
    public Result<?> handleExpectedException(Exception e) {
        logger.warn("Request invalid - '{}'", e.getMessage());
        return Result.error("Request invalid: " + e.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    public Result<?> handleAccessDeniedException(Exception e) {
        return Result.error("Operation not allowed");
    }

    @ExceptionHandler({ExceededMaxAdminCountException.class})
    @ResponseBody
    public Result<?> handleExceededMaxAminCountException(Exception e) {
        return Result.error("Maximum number of admin is exceeded");
    }

    @ExceptionHandler({UserRegisteredException.class})
    @ResponseBody
    public Result<?> handleUserRegisteredException(Exception e) {
        return Result.error("User registered already");
    }

    @ExceptionHandler({InvalidAuthenticationException.class})
    @ResponseBody
    public Result<?> handleInvalidAuthenticationException(Exception e) {
        return Result.error("Please login first");
    }

    @ExceptionHandler({MsgEmbeddedException.class})
    @ResponseBody
    public Result<?> handleMsgEmbeddedException(MsgEmbeddedException e) {
        String errorMsg = e.getMsg();
        if (!StringUtils.hasText(errorMsg)) {
            errorMsg = "Invalid parameters";
        }
        return Result.error(errorMsg);
    }

}
