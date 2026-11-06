package com.kaoyan.module.auth.controller;

import com.kaoyan.common.result.Result;
import com.kaoyan.module.auth.dto.LoginDTO;
import com.kaoyan.module.auth.dto.RegisterDTO;
import com.kaoyan.module.auth.service.AuthService;
import com.kaoyan.module.auth.vo.LoginVO;
import com.kaoyan.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "01-认证")
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation("注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success("注册成功", authService.register(dto));
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success("登录成功", authService.login(dto));
    }

    @ApiOperation("当前登录用户信息")
    @GetMapping("/me")
    public Result<LoginVO.UserInfo> me() {
        return Result.success(authService.currentUser(UserContext.userId()));
    }

    @ApiOperation("登出（前端清除 token 即可，此处仅做记录）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已登出", null);
    }
}
