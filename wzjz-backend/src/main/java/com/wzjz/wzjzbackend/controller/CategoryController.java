package com.wzjz.wzjzbackend.controller;

import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Category; // 需新建对应Entity
import com.wzjz.wzjzbackend.service.CategoryService; // 需新建
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getList() {
        return Result.success(categoryService.list());
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Category category) {
        categoryService.saveOrUpdate(category);
        return Result.success("分类更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("分类删除成功");
    }
}