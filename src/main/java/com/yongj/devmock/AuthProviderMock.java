package com.yongj.devmock;

import com.curtisnewbie.module.auth.processing.GenericAuthenticationProvider;
import com.curtisnewbie.service.auth.remote.consts.UserRole;
import com.curtisnewbie.service.auth.remote.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Mock bean that replaces {@link GenericAuthenticationProvider} for dev profile
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Profile("dev")
@Component
@Primary
@ConditionalOnProperty(value = "auth-service.mock.is-active", havingValue = "true")
public class AuthProviderMock extends GenericAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.isAuthenticated())
            return authentication;

        UserVo uv = new UserVo();
        uv.setId(3);
        uv.setUsername("zhuangyongj");
        uv.setRole(UserRole.ADMIN.getValue());

        log.info("Dev profile mock - authenticated user: {}", uv.toString());

        return new UsernamePasswordAuthenticationToken(uv,
                "123456",
                Arrays.asList(new SimpleGrantedAuthority(uv.getRole())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
