package com.yongj.web;

import com.curtisnewbie.module.auth.consts.UserRole;
import com.curtisnewbie.module.auth.dao.RegisterUserDto;
import com.curtisnewbie.module.auth.dao.UserEntity;
import com.curtisnewbie.module.auth.dao.UserInfo;
import com.curtisnewbie.module.auth.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.module.auth.exception.UserRegisteredException;
import com.curtisnewbie.module.auth.services.api.UserService;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.module.auth.util.PasswordUtil;
import com.yongj.dto.Resp;
import com.yongj.exceptions.ParamInvalidException;
import com.yongj.util.ValidUtils;
import com.yongj.vo.RegisterUserVo;
import com.yongj.vo.UpdatePasswordVo;
import com.yongj.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/list")
    public Resp<List<UserVo>> getUserList() {
        return Resp.of(toUserVoList(userService.findUserInfoList(), AuthUtil.getUserEntity().getId()));
    }

    @GetMapping("/info")
    public Resp<UserVo> getUserInfo() {
        UserEntity ue = AuthUtil.getUserEntity();
        return Resp.of(toUserVo(ue));
    }

    @PostMapping("/password/update")
    public Resp<Void> updatePassword(@RequestBody UpdatePasswordVo vo) throws ParamInvalidException {
        ValidUtils.requireNonNull(vo.getNewPassword());
        ValidUtils.requireNonNull(vo.getPrevPassword());
        if (Objects.equals(vo.getNewPassword(), vo.getPrevPassword()))
            return Resp.error("New password must be different");

        UserEntity ue = AuthUtil.getUserEntity();
        boolean isPasswordMatched = PasswordUtil.getValidator()
                .givenPassword(vo.getPrevPassword())
                .compareTo(ue.getPassword())
                .withSalt(ue.getSalt())
                .isMatched();
        if (!isPasswordMatched)
            return Resp.error("Password incorrect");
        userService.updatePassword(vo.getNewPassword(), ue.getUsername(), ue.getId());
        return Resp.ok();
    }


    private UserVo toUserVo(UserEntity ue) {
        UserVo uv = new UserVo();
        uv.setUsername(ue.getUsername());
        uv.setRole(ue.getRole());
        return uv;
    }

    private List<UserVo> toUserVoList(List<UserInfo> userInfoList, int currUserId) {
        return userInfoList.stream().filter(ui -> {
            // exclude current user
            return !Objects.equals(ui.getId(), currUserId);
        }).map(ui -> {
            UserVo uv = new UserVo();
            BeanUtils.copyProperties(ui, uv);
            return uv;
        }).collect(Collectors.toList());
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
