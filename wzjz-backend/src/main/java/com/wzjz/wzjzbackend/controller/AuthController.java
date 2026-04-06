package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.SysUser;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// 允许跨域请求
@CrossOrigin
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser sysUser) {
        try {
            // 将前端传过来的 phone, password, role 一起传给 service 进行多重校验
            SysUser user = sysUserService.login(sysUser.getPhone(), sysUser.getPassword(), sysUser.getRole());
            return Result.success(user);
        } catch (Exception e) {
            // 捕获 service 层抛出的各种异常提示，并返回给前端
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser sysUser) {
        try {
            sysUserService.register(sysUser);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}