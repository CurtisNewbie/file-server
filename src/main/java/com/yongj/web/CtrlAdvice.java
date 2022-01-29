package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.remote.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.exception.UserRegisteredException;
import com.yongj.exceptions.DuplicateExtException;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ExceptionHandler({IllegalPathException.class})
    @ResponseBody
    public Result<?> handleExpectedException(Exception e) {
        logger.warn("Request invalid - '{}'", e.getMessage());
        return Result.error("Request invalid: " + e.getMessage());
    }

    @ExceptionHandler({IllegalExtException.class})
    @ResponseBody
    public Result<?> handleIllegalExtException(Exception e) {
        logger.warn("Illegal file extension - '{}'", e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler({DuplicateExtException.class})
    @ResponseBody
    public Result<?> handleDuplicateExtException(Exception e) {
        logger.warn("Duplicate file extension - '{}'", e.getMessage());
        return Result.error(e.getMessage());
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
