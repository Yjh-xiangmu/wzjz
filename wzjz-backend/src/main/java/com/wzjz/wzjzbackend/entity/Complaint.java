package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_complaint")
public class Complaint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private Integer status; // 0待处理, 1已处理
    private String reply;
    private LocalDateTime createTime;
}