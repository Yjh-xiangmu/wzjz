package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Demand;
import com.wzjz.wzjzbackend.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/demand")
@CrossOrigin
public class DemandController {

    @Autowired
    private DemandService demandService;

    // 发布需求
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Demand demand) {
        demand.setStatus(0); // 默认待审核
        boolean saved = demandService.save(demand);
        return saved ? Result.success("需求发布成功，请等待管理员审核") : Result.error("发布失败");
    }

    // 获取当前受助方的需求列表
    @GetMapping("/myList")
    public Result<List<Demand>> getMyList(@RequestParam Long recipientId) {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Demand::getRecipientId, recipientId).orderByDesc(Demand::getCreateTime);
        return Result.success(demandService.list(wrapper));
    }
    // 新增：获取需求大厅列表 (只展示 status=1 求助中的需求)
    @GetMapping("/hallList")
    public Result<List<Demand>> getHallList() {
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Demand::getStatus, 1).orderByDesc(Demand::getCreateTime);
        return Result.success(demandService.list(wrapper));
    }
}