package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Message;
import com.wzjz.wzjzbackend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Result<String> send(@RequestBody Message message) {
        if (message.getOrderId() == null) {
            message.setOrderId(0L); // 默认0，不再强依赖订单
        }
        message.setIsRead(0);
        messageService.save(message);
        return Result.success("发送成功");
    }

    // 获取两个人之间的完整聊天记录
    @GetMapping("/history")
    public Result<List<Message>> history(@RequestParam Long userId1, @RequestParam Long userId2) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId1).eq(Message::getReceiverId, userId2))
                .or(w -> w.eq(Message::getSenderId, userId2).eq(Message::getReceiverId, userId1))
                .orderByAsc(Message::getCreateTime);
        return Result.success(messageService.list(wrapper));
    }
}