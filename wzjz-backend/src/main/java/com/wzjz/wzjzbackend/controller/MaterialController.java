package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Material;
import com.wzjz.wzjzbackend.mapper.MaterialMapper;
import com.wzjz.wzjzbackend.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/material")
@CrossOrigin

public class MaterialController {
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private MaterialService materialService;

    // 之前写的发布接口
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Material material) {
        material.setStatus(0);
        boolean saved = materialService.save(material);
        if (saved) {
            return Result.success("物资发布信息已保存");
        } else {
            return Result.error("数据库保存失败");
        }
    }

    // 新增：查询当前捐助者的发布记录
    @GetMapping("/myList")
    public Result<List<Material>> getMyList(@RequestParam Long donorId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        // 按照创建时间倒序排列，新发的在最前面
        wrapper.eq(Material::getDonorId, donorId).orderByDesc(Material::getCreateTime);
        List<Material> list = materialService.list(wrapper);
        return Result.success(list);
    }
    // 查询单条物资详细信息
    @GetMapping("/{id}")
    public Result<Material> getDetail(@PathVariable Long id) {
        Material material = materialService.getById(id);
        if (material == null) {
            return Result.error("物资信息不存在");
        }
        return Result.success(material);
    }
    // 新增：查询捐助者当前处于“待捐助”(status=0)的可用物资
    @GetMapping("/available")
    public Result<List<Material>> getAvailableList(@RequestParam Long donorId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getDonorId, donorId)
                .eq(Material::getStatus, 0)
                .orderByDesc(Material::getCreateTime);
        return Result.success(materialService.list(wrapper));
    }
    // 管理员：获取物资管理列表
    @GetMapping("/admin/list")
    public Result<List<Map<String, Object>>> getAdminList(@RequestParam(required = false) Integer status) {
        return Result.success(materialMapper.getAdminMaterialList(status));
    }

    // 管理员：强制下架或恢复物资
    // 状态码参考：0待捐助, 1已匹配待发货, 2运输中, 3已签收, 4强制下架
    @PostMapping("/admin/changeStatus")
    public Result<String> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        Material material = new Material();
        material.setId(id);
        material.setStatus(status);
        // 如果你的 MaterialService 名字不一样，请对应修改
        boolean updated = materialService.updateById(material);
        return updated ? Result.success("物资状态更新成功") : Result.error("状态更新失败");
    }
}