package com.wzjz.wzjzbackend.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Material;
import com.wzjz.wzjzbackend.mapper.MaterialMapper;
import com.wzjz.wzjzbackend.service.MaterialService;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {}