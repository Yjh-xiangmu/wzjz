package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Material;
import com.wzjz.wzjzbackend.entity.Order;
import com.wzjz.wzjzbackend.entity.SysUser;
import com.wzjz.wzjzbackend.mapper.OrderMapper;
import com.wzjz.wzjzbackend.service.MaterialService;
import com.wzjz.wzjzbackend.service.OrderService;
import com.wzjz.wzjzbackend.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody Order order) {
        try {
            orderService.createDonateOrder(order);
            return Result.success("捐助匹配成功，请尽快安排发货");
        } catch (Exception e) {
            return Result.error("捐助匹配失败: " + e.getMessage());
        }
    }

    @GetMapping("/recipientList")
    public Result<List<Map<String, Object>>> getRecipientList(@RequestParam Long recipientId) {
        List<Map<String, Object>> list = orderMapper.getRecipientOrders(recipientId);
        return Result.success(list);
    }

    @PostMapping("/confirmReceive/{orderId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> confirmReceive(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        order.setLogisticsStatus(2);
        orderService.updateById(order);

        Material material = new Material();
        material.setId(order.getMaterialId());
        material.setStatus(3);
        materialService.updateById(material);

        SysUser donor = sysUserService.getById(order.getDonorId());
        int currentPoints = donor.getPoints() == null ? 0 : donor.getPoints();
        donor.setPoints(currentPoints + 100);
        sysUserService.updateById(donor);

        return Result.success("签收成功！爱心积分已发放给捐助者。");
    }

    @PostMapping("/ship")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> shipOrder(@RequestBody Order orderParams) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMaterialId, orderParams.getMaterialId());
        Order order = orderService.getOne(wrapper);

        if (order != null) {
            order.setLogisticsStatus(1);
            order.setLogisticsCompany(orderParams.getLogisticsCompany());
            order.setLogisticsNo(orderParams.getLogisticsNo());
            orderService.updateById(order);
        }

        Material material = materialService.getById(orderParams.getMaterialId());
        material.setStatus(2);
        materialService.updateById(material);

        return Result.success("发货成功，物流信息已同步给受助方！");
    }

    // ================= 以下是重构后的联系人与沟通记录接口 =================

    @GetMapping("/donorContacts")
    public Result<List<Map<String, Object>>> getDonorContacts(@RequestParam Long donorId) {
        return Result.success(orderMapper.getDonorContacts(donorId));
    }

    @GetMapping("/recipientContacts")
    public Result<List<Map<String, Object>>> getRecipientContacts(@RequestParam Long recipientId) {
        return Result.success(orderMapper.getRecipientContacts(recipientId));
    }

    @GetMapping("/recordsBetween")
    public Result<List<Map<String, Object>>> getRecordsBetween(@RequestParam Long donorId, @RequestParam Long recipientId) {
        return Result.success(orderMapper.getOrdersBetween(donorId, recipientId));
    }
    // ================= 管理员端：全局订单与物流监控 =================
    @GetMapping("/admin/list")
    public Result<List<Order>> getAllOrders() {
        // 全局查看所有订单及其物流状态，按生成时间倒序
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.list(wrapper));
    }
}