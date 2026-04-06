package com.wzjz.wzjzbackend.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Feedback;
import com.wzjz.wzjzbackend.mapper.FeedbackMapper;
import com.wzjz.wzjzbackend.service.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {}