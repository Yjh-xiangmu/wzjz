package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String password;

    private String role;

    private Integer status;

    private String qualifications;

    // 新增：爱心积分字段
    private Integer points;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    private String orgName;
    private String orgDescription;
}