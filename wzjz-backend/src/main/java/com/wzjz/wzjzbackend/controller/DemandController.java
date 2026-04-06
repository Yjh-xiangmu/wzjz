package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Demand;
import com.wzjz.wzjzbackend.mapper.DemandMapper;
import com.wzjz.wzjzbackend.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demand")
@CrossOrigin
public class DemandController {

    @Autowired
    private DemandService demandService;

    @Autowired
    private DemandMapper demandMapper;

    // 受助方：发布需求
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Demand demand) {
        demand.setStatus(0); // 刚发布默认是待审核状态(0)
        boolean saved = demandService.save(demand);
        return saved ? Result.success("发布成功") : Result.error("发布失败");
    }

    // 受助方：获取自己的需求列表
    @GetMapping("/myList")
    public Result<List<Demand>> getMyList(@RequestParam Long recipientId) {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Demand::getRecipientId, recipientId)
                .orderByDesc(Demand::getCreateTime);
        return Result.success(demandService.list(wrapper));
    }

    // 捐助者：获取需求大厅列表 (只展示 status=1 求助中的需求)
    @GetMapping("/hallList")
    public Result<List<Demand>> getHallList() {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Demand::getStatus, 1).orderByDesc(Demand::getCreateTime);
        return Result.success(demandService.list(wrapper));
    }

    // ================== 管理员专属接口 ==================

    // 管理员：获取需求项目列表（包含受助方信息的多表联查）
    @GetMapping("/admin/list")
    public Result<List<Map<String, Object>>> getAdminList(@RequestParam(required = false) Integer status) {
        return Result.success(demandMapper.getAdminDemandList(status));
    }

    // 管理员：审核或更改需求状态
    // status: 1(通过/求助中), 3(驳回/下架)
    @PostMapping("/admin/changeStatus")
    public Result<String> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        Demand demand = new Demand();
        demand.setId(id);
        demand.setStatus(status);
        boolean updated = demandService.updateById(demand);
        return updated ? Result.success("项目状态更新成功") : Result.error("状态更新失败");
    }
}