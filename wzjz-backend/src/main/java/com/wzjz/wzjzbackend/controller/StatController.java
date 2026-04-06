package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.mapper.OrderMapper;
import com.wzjz.wzjzbackend.service.DemandService;
import com.wzjz.wzjzbackend.service.MaterialService;
import com.wzjz.wzjzbackend.service.OrderService;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stat")
@CrossOrigin
public class StatController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private DemandService demandService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    // 获取平台四大核心数据汇总
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", sysUserService.count());
        map.put("materialCount", materialService.count());
        map.put("demandCount", demandService.count());
        map.put("orderCount", orderService.count());
        return Result.success(map);
    }

    // 获取最新匹配动态
    @GetMapping("/dynamics")
    public Result<List<Map<String, Object>>> getDynamics() {
        return Result.success(orderMapper.getLatestDynamics());
    }
}