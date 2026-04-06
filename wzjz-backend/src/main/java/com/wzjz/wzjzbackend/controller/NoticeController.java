package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Notice; // 需新建对应Entity
import com.wzjz.wzjzbackend.service.NoticeService; // 需新建
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public Result<List<Notice>> getList() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Notice::getCreateTime);
        return Result.success(noticeService.list(wrapper));
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Notice notice) {
        noticeService.saveOrUpdate(notice);
        return Result.success("公告保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("公告删除成功");
    }
}