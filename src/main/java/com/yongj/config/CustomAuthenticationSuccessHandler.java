package com.yongj.config;

import com.curtisnewbie.module.auth.config.AuthenticationSuccessHandlerExtender;
import com.curtisnewbie.common.vo.Resp;
import com.curtisnewbie.common.util.JsonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yongjie.zhuang
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandlerExtender {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.getWriter().write(JsonUtils.writeValueAsString(Resp.ok()));
    }

}
