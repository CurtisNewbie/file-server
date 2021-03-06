package com.yongj.web;

import com.curtisnewbie.common.vo.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author yongjie.zhuang
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.yongj.web.*Controller.*(..))")
    public Object logBeforeOperation(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        Object result = null;
        try {
            sw.start();
            result = pjp.proceed();
            return result;
        } finally {
            sw.stop();
            logger.info("JoinPoint: '{}', arguments: {}, took '{}' millisec, result: {}",
                    pjp.toShortString(),
                    cvtToStr(pjp.getArgs()),
                    sw.getTotalTimeMillis(),
                    respToStr(result));
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

    private static final String respToStr(Object o) {
        if (o == null)
            return "null";

        if (o instanceof ResponseEntity) {
            ResponseEntity respEntity = (ResponseEntity) o;
            StringBuilder sb = new StringBuilder("@ResponseEntity{ ");
            sb.append("statusCode: ").append(respEntity.getStatusCode()).append(", ");
            sb.append("body: ");
            if (respEntity.getBody() == null) {
                sb.append("null");
            } else {
                if (respEntity.getBody() instanceof byte[]) {
                    sb.append(((byte[]) respEntity.getBody()).length + " bytes");
                } else if (respEntity.getBody() instanceof Result) {
                    Result r = Result.class.cast(respEntity.getBody());
                    sb.append(resultToStr(r));
                } else {
                    sb.append(respEntity.getBody().toString());
                }
            }
            sb.append(" }");
            return sb.toString();
        } else if (o instanceof Result) {
            Result r = Result.class.cast(o);
            return resultToStr(r);
        } else {
            return o.toString();
        }
    }

    private static final String resultToStr(Result r) {
        StringBuilder sb = new StringBuilder("@Result{ ");
        sb.append("hasError: ").append(r.isHasError()).append(", ");
        sb.append("msg: ").append(r.getMsg()).append(", ");
        sb.append("data: ").append(r.getData() == null ? "null" : "...").append(" }");
        return sb.toString();
    }
}
