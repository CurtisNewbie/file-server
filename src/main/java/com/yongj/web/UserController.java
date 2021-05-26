package com.yongj.web;

import com.curtisnewbie.module.auth.consts.UserRole;
import com.curtisnewbie.module.auth.dao.RegisterUserDto;
import com.curtisnewbie.module.auth.dao.UserEntity;
import com.curtisnewbie.module.auth.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.module.auth.exception.UserRegisteredException;
import com.curtisnewbie.module.auth.services.api.UserService;
import com.curtisnewbie.module.auth.vo.RegisterUserVo;
import com.yongj.UserVo;
import com.yongj.dto.Resp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/register/guest")
    public Resp<?> guestRegister(@RequestBody RegisterUserVo registerUserVo) throws UserRegisteredException,
            ExceededMaxAdminCountException {
        RegisterUserDto dto = new RegisterUserDto();
        BeanUtils.copyProperties(registerUserVo, dto);
        dto.setRole(UserRole.GUEST.val);
        userService.register(dto);
        return Resp.ok();
    }

    @GetMapping("/info")
    public Resp<UserVo> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return Resp.error("Please login first");
        if (auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserEntity))
            throw new IllegalStateException("Authentication#principal is null or not instance of UserEntity");
        UserEntity ue = UserEntity.class.cast(auth.getPrincipal());
        return Resp.of(toUserVo(ue));
    }

    private UserVo toUserVo(UserEntity ue) {
        UserVo uv = new UserVo();
        uv.setUsername(ue.getUsername());
        uv.setRole(ue.getRole());
        return uv;
    }

//
//    @PostMapping("/admin")
//    public ResponseEntity<?> adminRegister(RegisterUserVo registerUserVo) throws UserRegisteredException,
//            ExceededMaxAdminCountException {
//        RegisterUserDto dto = new RegisterUserDto();
//        BeanUtils.copyProperties(registerUserVo, dto);
//        dto.setRole(Role.ADMIN.val);
//        userService.register(dto);
//        return ResponseEntity.ok().build();
//    }
}
