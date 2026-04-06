package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Notice;
import com.wzjz.wzjzbackend.mapper.NoticeMapper;
import com.wzjz.wzjzbackend.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}