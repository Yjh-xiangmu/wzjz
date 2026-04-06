package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.SysUser;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // 之前写的获取信息接口
    @GetMapping("/info")
    public Result<SysUser> getUserInfo(@RequestParam Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    // 新增：修改密码接口
    @PostMapping("/updatePwd")
    public Result<String> updatePwd(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String oldPwd = params.get("oldPwd").toString();
        String newPwd = params.get("newPwd").toString();

        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 校验原密码
        if (!user.getPassword().equals(oldPwd)) {
            return Result.error("原密码错误");
        }

        // 更新新密码
        user.setPassword(newPwd);
        sysUserService.updateById(user);

        return Result.success("密码修改成功，请妥善保管");
    }
    // 新增：保存或更新机构主页信息
    @PostMapping("/updateOrg")
    public Result<String> updateOrg(@RequestBody SysUser user) {
        // 使用 updateById 会自动根据传入的 ID 更新非空字段
        boolean updated = sysUserService.updateById(user);
        if (updated) {
            return Result.success("机构信息保存成功");
        } else {
            return Result.error("保存失败");
        }
    }
    // 管理员：获取全量用户列表（可根据角色和状态筛选）
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
        // 清除密码信息返回前端
        list.forEach(user -> user.setPassword(null));
        return Result.success(list);
    }

    // 管理员：修改用户状态（审核通过、驳回、封禁）
    // status: 0正常, 1禁用/驳回, 2待审核
    @PostMapping("/admin/changeStatus")
    public Result<String> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        boolean updated = sysUserService.updateById(user);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}