package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.SysUser;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/info")
    public Result<SysUser> getUserInfo(@RequestParam Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping("/updatePwd")
    public Result<String> updatePwd(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String oldPwd = params.get("oldPwd").toString();
        String newPwd = params.get("newPwd").toString();

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("原密码错误");
        }

        user.setPassword(newPwd);
        sysUserService.updateById(user);

        return Result.success("密码修改成功，请妥善保管");
    }

    @PostMapping("/updateOrg")
    public Result<String> updateOrg(@RequestBody SysUser user) {
        boolean updated = sysUserService.updateById(user);
        if (updated) {
            return Result.success("机构信息保存成功");
        } else {
            return Result.error("保存失败");
        }
    }

    @GetMapping("/admin/list")
    public Result<List<SysUser>> getUserList(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        List<SysUser> list = sysUserService.list(wrapper);
        list.forEach(user -> user.setPassword(null));
        return Result.success(list);
    }

    @PostMapping("/admin/changeStatus")
    public Result<String> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        boolean updated = sysUserService.updateById(user);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}