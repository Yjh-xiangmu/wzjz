package com.wzjz.wzjzbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzjz.wzjzbackend.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    // 增加 role 参数
    SysUser login(String phone, String password, String role);
    void register(SysUser sysUser);
}