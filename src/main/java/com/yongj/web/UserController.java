package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.consts.UserRole;
import com.curtisnewbie.module.auth.dao.UserEntity;
import com.curtisnewbie.module.auth.exception.UserRelatedException;
import com.curtisnewbie.module.auth.services.api.UserService;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.module.auth.vo.RegisterUserVo;
import com.curtisnewbie.module.auth.vo.UserInfoVo;
import com.yongj.vo.DisableUserById;
import com.yongj.vo.UpdatePasswordVo;
import com.yongj.vo.UserInfoFsVo;
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
@RequestMapping("${web.base-path}/user")
public class UserController {

    private static final int PASSWORD_LENGTH = 6;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/register")
    public Result<?> addUser(@RequestBody com.yongj.vo.RegisterUserVo registerUserVo) throws UserRelatedException,
            MsgEmbeddedException {
        RegisterUserVo dto = new RegisterUserVo();
        BeanUtils.copyProperties(registerUserVo, dto);

        // validate whether username and password is entered
        ValidUtils.requireNotEmpty(dto.getUsername(), "Please enter username");
        ValidUtils.requireNotEmpty(dto.getPassword(), "Please enter password");

        // validate if the username and password is the same
        ValidUtils.requireNotEquals(dto.getUsername(), dto.getPassword(), "Username and password must be different");

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
    public Result<List<UserInfoFsVo>> getUserList() {
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

    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNotEmpty(vo.getNewPassword());
        ValidUtils.requireNotEmpty(vo.getPrevPassword());

        // check if the old password and prev password are equal
        ValidUtils.requireNotEquals(vo.getNewPassword(), vo.getPrevPassword(), "New password must be different");

        // validate if the new password is too short
        if (vo.getNewPassword().length() < PASSWORD_LENGTH)
            return Result.error("Password must have at least " + PASSWORD_LENGTH + "characters");

        UserEntity ue = AuthUtil.getUserEntity();
        try {
            userService.updatePassword(vo.getNewPassword(), vo.getPrevPassword(), ue.getId());
        } catch (UserRelatedException ignore) {
            return Result.error("Password incorrect");
        }
        return Result.ok();
    }

    private List<UserInfoFsVo> toUserInfoVoList(List<UserInfoVo> userInfoList, int currUserId) {
        return userInfoList.stream().filter(ui -> {
            // exclude current user
            return !Objects.equals(ui.getId(), currUserId);
        }).map(ui -> BeanCopyUtils.toType(ui, UserInfoFsVo.class))
                .collect(Collectors.toList());
    }
}
