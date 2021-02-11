package com.yongj.web;

import com.yongj.dto.Resp;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Resp<?>> handleGeneralException(Exception e) {
        logger.error("Exception occurred", e);
        return ResponseEntity.ok(Resp.error("Internal Error"));
    }

    // TODO This doesn't really wrap the exception with a custom response
    @ExceptionHandler({MaxUploadSizeExceededException.class, SizeLimitExceededException.class })
    @ResponseBody
    public ResponseEntity<Resp<?>> handleSizeLimitExceededException(Exception e) {
        logger.warn("Size limit exceeded - '{}'", e.getMessage());
        return ResponseEntity.ok(Resp.error("Size limit exceeded"));
    }

    @ExceptionHandler({IllegalExtException.class, IllegalPathException.class})
    @ResponseBody
    public ResponseEntity<Resp<?>> handleExpectedException(Exception e) {
        logger.warn("Request invalid - '{}'", e.getMessage());
        return ResponseEntity.ok(Resp.error("Request invalid: " + e.getMessage()));
    }

}
