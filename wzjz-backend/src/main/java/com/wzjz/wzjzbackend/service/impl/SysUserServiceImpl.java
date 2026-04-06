package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.SysUser;
import com.wzjz.wzjzbackend.mapper.SysUserMapper;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser login(String phone, String password, String role) {
        // 1. 先只根据手机号查出用户，判断账号是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        SysUser user = this.getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("该手机号尚未注册");
        }

        // 2. 判断密码是否正确
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(md5Password)) {
            throw new RuntimeException("登录密码错误");
        }

        // 3. 判断登录选择的角色和数据库是否一致
        if (!user.getRole().equals(role)) {
            throw new RuntimeException("选择的登录角色与该账号不匹配");
        }

        // 4. 判断账号状态
        if (user.getStatus() == 1) {
            throw new RuntimeException("该账号已被禁用，请联系管理员解禁");
        }
        if (user.getStatus() == 2) {
            throw new RuntimeException("受助方资质正在审核中，请耐心等待管理员通过");
        }

        // 验证全部通过，抹除密码返回，保证安全
        user.setPassword(null);
        return user;
    }

    @Override
    public void register(SysUser sysUser) {
        // 检查手机号是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, sysUser.getPhone());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("该手机号已注册");
        }

        // 密码加密保存
        String md5Password = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
        sysUser.setPassword(md5Password);

        // 如果是受助方(recipient)，状态设为2(待审核)，否则设为0(正常)
        if ("recipient".equals(sysUser.getRole())) {
            sysUser.setStatus(2);
        } else {
            sysUser.setStatus(0);
        }

        this.save(sysUser);
    }
}