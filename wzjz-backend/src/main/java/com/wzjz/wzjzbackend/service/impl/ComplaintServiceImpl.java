package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Complaint;
import com.wzjz.wzjzbackend.mapper.ComplaintMapper;
import com.wzjz.wzjzbackend.service.ComplaintService;
import org.springframework.stereotype.Service;

@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
}