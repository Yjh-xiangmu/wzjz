package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("biz_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long demandId;
    private Long materialId;
    private Long donorId;
    private Long recipientId;
    private Integer logisticsStatus;
    private String logisticsCompany;
    private String logisticsNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}