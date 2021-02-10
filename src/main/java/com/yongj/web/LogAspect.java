package com.yongj.web;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author yongjie.zhuang
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.yongj.web.FileController.*(..))")
    public void logBeforeOperation(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        Object result = null;
        try {
            sw.start();
            result = pjp.proceed();
        } finally {
            sw.stop();
            logger.info("JoinPoint: '{}', arguments: {}, took '{}' millisec, result: {}",
                    pjp.toShortString(),
                    cvtToStr(pjp.getArgs()),
                    sw.getTotalTimeMillis(),
                    result == null ? "unknown" : result);
        }
    }

    private static final String cvtToStr(Object[] args) {
        if (args == null)
            return "[ null ]";

        StringBuilder sb = new StringBuilder();
        for (Object o : args) {
            if (isPrimitiveType(o)) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(o == null ? "null" : "'" + o.toString() + "'");
            }
        }
        sb.insert(0, "[ ");
        sb.append(" ]");
        return sb.toString();
    }

    private static final boolean isPrimitiveType(Object o) {
        return o == null
                || o instanceof String
                || o instanceof Integer
                || o instanceof Short
                || o instanceof Long
                || o instanceof Double
                || o instanceof Float;
    }
}
