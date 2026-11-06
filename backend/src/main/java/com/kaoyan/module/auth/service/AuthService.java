package com.kaoyan.module.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kaoyan.common.enums.RoleCode;
import com.kaoyan.common.exception.BizException;
import com.kaoyan.common.result.ResultCode;
import com.kaoyan.common.util.JwtUtil;
import com.kaoyan.module.auth.dto.LoginDTO;
import com.kaoyan.module.auth.dto.RegisterDTO;
import com.kaoyan.module.auth.vo.LoginVO;
import com.kaoyan.module.system.entity.SysRole;
import com.kaoyan.module.system.entity.SysUser;
import com.kaoyan.module.system.entity.SysUserRole;
import com.kaoyan.module.system.mapper.SysRoleMapper;
import com.kaoyan.module.system.mapper.SysUserMapper;
import com.kaoyan.module.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterDTO dto) {
        Long exists = userMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (exists != null && exists > 0) {
            throw BizException.of(ResultCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        // 默认赋予学生角色
        SysRole role = roleMapper.selectOne(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getRoleCode, RoleCode.STUDENT.getCode())
                .last("limit 1"));
        if (role != null) {
            userRoleMapper.insert(new SysUserRole(user.getId(), role.getId()));
        } else {
            log.warn("未找到 STUDENT 角色，请先执行 sql/init-data.sql 初始化角色数据");
        }
        log.info("新用户注册成功: id={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    public LoginVO login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername())
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw BizException.of(ResultCode.USER_PASSWORD_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw BizException.of(ResultCode.USER_DISABLED);
        }

        List<String> roles = roleMapper.selectRoleCodesByUserId(user.getId());
        if (roles.isEmpty()) {
            roles = List.of(RoleCode.STUDENT.getCode());
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        return LoginVO.builder()
                .token(token)
                .expireMinutes(jwtUtil.getExpireMinutes())
                .user(LoginVO.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .roles(roles)
                        .build())
                .build();
    }

    public LoginVO.UserInfo currentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw BizException.of(ResultCode.USER_NOT_EXIST);
        }
        return LoginVO.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(roleMapper.selectRoleCodesByUserId(userId))
                .build();
    }
}
