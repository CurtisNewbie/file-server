package com.yongj.web;

import com.curtisnewbie.module.auth.consts.UserRole;
import com.curtisnewbie.module.auth.dao.RegisterUserDto;
import com.curtisnewbie.module.auth.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.module.auth.exception.UserRegisteredException;
import com.curtisnewbie.module.auth.services.api.UserService;
import com.curtisnewbie.module.auth.vo.RegisterUserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("/user/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/guest")
    public ResponseEntity<?> guestRegister(@RequestBody RegisterUserVo registerUserVo) throws UserRegisteredException,
            ExceededMaxAdminCountException {
        RegisterUserDto dto = new RegisterUserDto();
        BeanUtils.copyProperties(registerUserVo, dto);
        dto.setRole(UserRole.GUEST.val);
        userService.register(dto);
        return ResponseEntity.ok().build();
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
