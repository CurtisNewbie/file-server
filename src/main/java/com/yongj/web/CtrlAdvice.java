package com.yongj.web;

import com.curtisnewbie.module.auth.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.module.auth.exception.InvalidAuthenticationException;
import com.curtisnewbie.module.auth.exception.UserRegisteredException;
import com.curtisnewbie.common.vo.Result;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Result<?>> handleGeneralException(Exception e) {
        logger.error("Exception occurred", e);
        return ResponseEntity.ok(Result.error("Internal Error"));
    }

    // TODO This doesn't really wrap the exception with a custom response
    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleSizeLimitExceededException(Exception e) {
        logger.warn("Size limit exceeded - '{}'", e.getMessage());
        return ResponseEntity.ok(Result.error("Size limit exceeded"));
    }

    @ExceptionHandler({IllegalExtException.class, IllegalPathException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleExpectedException(Exception e) {
        logger.warn("Request invalid - '{}'", e.getMessage());
        return ResponseEntity.ok(Result.error("Request invalid: " + e.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleAccessDeniedException(Exception e) {
        return ResponseEntity.ok(Result.error("Operation not allowed"));
    }

    @ExceptionHandler({ExceededMaxAdminCountException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleExceededMaxAminCountException(Exception e) {
        return ResponseEntity.ok(Result.error("Maximum number of admin is exceeded"));
    }

    @ExceptionHandler({UserRegisteredException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleUserRegisteredException(Exception e) {
        return ResponseEntity.ok(Result.error("User registered already"));
    }

    @ExceptionHandler({InvalidAuthenticationException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleInvalidAuthenticationException(Exception e) {
        return ResponseEntity.ok(Result.error("Authentication invalid, please re-login"));
    }

    @ExceptionHandler({MsgEmbeddedException.class})
    @ResponseBody
    public ResponseEntity<Result<?>> handleMsgEmbeddedException(MsgEmbeddedException e) {
        String errorMsg = e.getMsg();
        if (!StringUtils.hasText(errorMsg)) {
            errorMsg = "Invalid parameters";
        }
        return ResponseEntity.ok(Result.error(errorMsg));
    }

}
