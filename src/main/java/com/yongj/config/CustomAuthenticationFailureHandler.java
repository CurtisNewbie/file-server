package com.yongj.config;

import com.curtisnewbie.module.auth.config.AuthenticationFailureHandlerExtender;
import com.curtisnewbie.common.vo.Resp;
import com.curtisnewbie.common.util.JsonUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yongjie.zhuang
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandlerExtender {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String errorMsg = "Incorrect credentials";
        if (exception instanceof DisabledException) {
            errorMsg = "User is disabled";
        }
        response.getWriter().write(JsonUtils.writeValueAsString(Resp.error(errorMsg)));
    }
}
