package com.wzjz.wzjzbackend.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Demand;
import com.wzjz.wzjzbackend.mapper.DemandMapper;
import com.wzjz.wzjzbackend.service.DemandService;
import org.springframework.stereotype.Service;

@Service
public class DemandServiceImpl extends ServiceImpl<DemandMapper, Demand> implements DemandService {}