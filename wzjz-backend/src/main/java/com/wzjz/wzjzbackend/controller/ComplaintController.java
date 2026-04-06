package com.wzjz.wzjzbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wzjz.wzjzbackend.common.Result;
import com.wzjz.wzjzbackend.entity.Complaint; // 需新建对应Entity
import com.wzjz.wzjzbackend.service.ComplaintService; // 需新建
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaint")
@CrossOrigin
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // 管理员获取所有投诉与建议
    @GetMapping("/admin/list")
    public Result<List<Complaint>> getList() {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Complaint::getCreateTime);
        return Result.success(complaintService.list(wrapper));
    }

    // 用户查看自己的反馈列表（含管理员回复）
    @GetMapping("/myList")
    public Result<List<Complaint>> getMyList(@RequestParam Long userId) {
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getUserId, userId).orderByDesc(Complaint::getCreateTime);
        return Result.success(complaintService.list(wrapper));
    }

    // 用户提交投诉或建议
    @PostMapping("/submit")
    public Result<String> submitComplaint(@RequestBody Complaint complaint) {
        complaint.setStatus(0);
        complaintService.save(complaint);
        return Result.success("提交成功");
    }

    // 管理员处理意见并回复
    @PostMapping("/handle")
    public Result<String> handleComplaint(@RequestBody Complaint complaint) {
        complaint.setStatus(1); // 标记为已处理
        complaintService.updateById(complaint);
        return Result.success("处理成功");
    }
}