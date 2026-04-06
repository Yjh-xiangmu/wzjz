package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Category;
import com.wzjz.wzjzbackend.mapper.CategoryMapper;
import com.wzjz.wzjzbackend.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}