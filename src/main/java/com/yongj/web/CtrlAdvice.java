package com.yongj.web;

import com.curtisnewbie.common.advice.GlobalControllerAdvice;
import com.curtisnewbie.common.vo.Result;
import com.yongj.exceptions.DuplicateExtException;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yongjie.zhuang
 */
@ControllerAdvice
public class CtrlAdvice extends GlobalControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(CtrlAdvice.class);

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
}
