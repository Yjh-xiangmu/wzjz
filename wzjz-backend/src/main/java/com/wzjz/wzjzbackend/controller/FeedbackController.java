package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Feedback;
import com.wzjz.wzjzbackend.entity.Order;
import com.wzjz.wzjzbackend.mapper.FeedbackMapper;
import com.wzjz.wzjzbackend.service.FeedbackService;
import com.wzjz.wzjzbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackMapper feedbackMapper;
    @Autowired
    private OrderService orderService;

    // 获取需要回馈的订单列表
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(@RequestParam Long recipientId) {
        return Result.success(feedbackMapper.getRecipientFeedbackList(recipientId));
    }

    // 提交感谢信
    @PostMapping("/submit")
    public Result<String> submitFeedback(@RequestBody Feedback feedback) {
        // 根据订单查出是哪个捐助者，补全 donorId
        Order order = orderService.getById(feedback.getOrderId());
        if (order != null) {
            feedback.setDonorId(order.getDonorId());
        }
        boolean saved = feedbackService.save(feedback);
        return saved ? Result.success("感恩回馈已发送，感谢您的用心！") : Result.error("发送失败");
    }
    // 新增：捐助者在物资详情里查看对应的感谢信
    @GetMapping("/getByMaterial")
    public Result<Feedback> getByMaterial(@RequestParam Long materialId) {
        return Result.success(feedbackMapper.getByMaterialId(materialId));
    }
}