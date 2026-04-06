package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("biz_feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long donorId;
    private Long recipientId;
    private String content;
    private String images;
    private LocalDateTime createTime;
}