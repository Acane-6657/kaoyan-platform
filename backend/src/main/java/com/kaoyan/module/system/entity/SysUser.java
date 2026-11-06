package com.kaoyan.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kaoyan.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@ApiModel("用户")
public class SysUser extends BaseEntity {

    @ApiModelProperty("登录名")
    private String username;

    @ApiModelProperty("密码(BCrypt)")
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("状态 1启用 0禁用")
    private Integer status;
}
