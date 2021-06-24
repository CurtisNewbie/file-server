package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.consts.UserRole;
import com.curtisnewbie.module.auth.dao.RegisterUserDto;
import com.curtisnewbie.module.auth.dao.UserEntity;
import com.curtisnewbie.module.auth.dao.UserInfo;
import com.curtisnewbie.module.auth.exception.ExceededMaxAdminCountException;
import com.curtisnewbie.module.auth.exception.UserRegisteredException;
import com.curtisnewbie.module.auth.services.api.UserService;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.module.auth.util.PasswordUtil;
import com.yongj.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
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

    private static final int PASSWORD_LENGTH = 6;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/register")
    public Result<?> addUser(@RequestBody RegisterUserVo registerUserVo) throws UserRegisteredException,
            ExceededMaxAdminCountException, MsgEmbeddedException {
        RegisterUserDto dto = new RegisterUserDto();
        BeanUtils.copyProperties(registerUserVo, dto);
        // validate whether username and password is entered
        if (!StringUtils.hasText(dto.getUsername()))
            return Result.error("Please enter username");
        if (!StringUtils.hasText(dto.getPassword()))
            return Result.error("Please enter password");
        // validate if the username and password is the same
        if (Objects.equals(dto.getUsername(), dto.getPassword()))
            return Result.error("Username and password must be different");
        // validate if the password is too short
        if (dto.getPassword().length() < PASSWORD_LENGTH)
            return Result.error("Password must have at least " + PASSWORD_LENGTH + "characters");

        // if not specified, the role will be guest
        UserRole role = UserRole.GUEST;
        if (registerUserVo.getUserRole() != null) {
            role = UserRole.parseUserRole(registerUserVo.getUserRole());
            ValidUtils.requireNonNull(role, "Illegal user role");
        }
        // do not support adding administrator
        if (Objects.equals(role, UserRole.ADMIN)) {
            return Result.error("Do not support adding administrator");
        }
        dto.setRole(role);
        dto.setCreateBy(AuthUtil.getUsername());
        userService.register(dto);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/list")
    public Result<List<UserInfoVo>> getUserList() {
        return Result.of(toUserInfoVoList(userService.findAllUserInfoList(), AuthUtil.getUserEntity().getId()));
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/disable")
    public Result<Void> disableUserById(@RequestBody DisableUserById param) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(param.getId());
        if (Objects.equals(param.getId(), AuthUtil.getUserEntity().getId())) {
            throw new MsgEmbeddedException("You cannot disable yourself");
        }
        userService.disableUserById(param.getId(), AuthUtil.getUsername());
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/enable")
    public Result<Void> enableUserById(@RequestBody DisableUserById param) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(param.getId());
        if (Objects.equals(param.getId(), AuthUtil.getUserEntity().getId())) {
            throw new MsgEmbeddedException("You cannot enable yourself");
        }
        userService.enableUserById(param.getId(), AuthUtil.getUsername());
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<UserVo> getUserInfo() {
        UserEntity ue = AuthUtil.getUserEntity();
        return Result.of(BeanCopyUtils.toType(ue, UserVo.class));
    }

    // TODO, not supported so far
//    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getNewPassword());
        ValidUtils.requireNonNull(vo.getPrevPassword());
        if (Objects.equals(vo.getNewPassword(), vo.getPrevPassword()))
            return Result.error("New password must be different");

        UserEntity ue = AuthUtil.getUserEntity();
        boolean isPasswordMatched = PasswordUtil.getValidator()
                .givenPasswordAndSalt(vo.getPrevPassword(), ue.getSalt())
                .compareTo(ue.getPassword())
                .isMatched();
        if (!isPasswordMatched)
            return Result.error("Password incorrect");
        userService.updatePassword(vo.getNewPassword(), ue.getUsername(), ue.getId());
        return Result.ok();
    }

    private List<UserInfoVo> toUserInfoVoList(List<UserInfo> userInfoList, int currUserId) {
        return userInfoList.stream().filter(ui -> {
            // exclude current user
            return !Objects.equals(ui.getId(), currUserId);
        }).map(ui -> BeanCopyUtils.toType(ui, UserInfoVo.class))
                .collect(Collectors.toList());
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
