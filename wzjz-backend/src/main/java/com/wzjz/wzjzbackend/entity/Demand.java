package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("biz_demand")
public class Demand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recipientId;
    private String title;
    private Integer quantity;

    // 新增：已匹配/筹集的数量
    private Integer matchedQuantity;

    private String urgency;
    private String background;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String address;
    private String contactPhone;
}