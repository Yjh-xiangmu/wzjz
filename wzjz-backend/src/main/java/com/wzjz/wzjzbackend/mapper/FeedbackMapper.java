package com.wzjz.wzjzbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzjz.wzjzbackend.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    // 查询受助方所有“已签收(状态2)”的订单，并关联查询是否已经写过感谢信
    @Select("SELECT o.id as order_id, o.create_time as order_time, " +
            "m.name as material_name, u.phone as donor_phone, " +
            "f.id as feedback_id, f.content, f.images, f.create_time as feedback_time " +
            "FROM biz_order o " +
            "LEFT JOIN biz_material m ON o.material_id = m.id " +
            "LEFT JOIN sys_user u ON o.donor_id = u.id " +
            "LEFT JOIN biz_feedback f ON o.id = f.order_id " +
            "WHERE o.recipient_id = #{recipientId} AND o.logistics_status = 2 " +
            "ORDER BY o.create_time DESC")
    List<Map<String, Object>> getRecipientFeedbackList(@Param("recipientId") Long recipientId);
    // 新增：根据物资ID查询感谢信
    @Select("SELECT f.* FROM biz_feedback f JOIN biz_order o ON f.order_id = o.id WHERE o.material_id = #{materialId}")
    Feedback getByMaterialId(@Param("materialId") Long materialId);
}