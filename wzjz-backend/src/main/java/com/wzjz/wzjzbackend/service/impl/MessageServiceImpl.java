package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Message;
import com.wzjz.wzjzbackend.mapper.MessageMapper;
import com.wzjz.wzjzbackend.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}