package com.wzjz.wzjzbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long donorId;
    private String name;
    private String category;
    private Integer quantity;
    private String conditionDegree;
    private LocalDate validityDate;
    private String description;
    private String images;
    private Integer status; // 0待捐助, 1已匹配, 2运输中, 3已完成, 4驳回
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}