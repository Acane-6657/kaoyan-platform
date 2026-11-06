package com.kaoyan.module.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@ApiModel("登录结果")
public class LoginVO {

    @ApiModelProperty("访问令牌")
    private String token;

    @ApiModelProperty("令牌有效期(分钟)")
    private Long expireMinutes;

    @ApiModelProperty("用户信息")
    private UserInfo user;

    @Data
    @Builder
    @ApiModel("用户信息")
    public static class UserInfo {
        @ApiModelProperty("用户ID")
        private Long id;
        @ApiModelProperty("登录名")
        private String username;
        @ApiModelProperty("昵称")
        private String nickname;
        @ApiModelProperty("头像")
        private String avatar;
        @ApiModelProperty("角色编码列表")
        private List<String> roles;
    }
}
