package com.wzjz.wzjzbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzjz.wzjzbackend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    // 之前用到的受助方接收列表
    @Select("SELECT o.id as order_id, o.logistics_status, o.create_time, " +
            "d.title as demand_title, m.name as material_name, m.images, u.phone as donor_phone " +
            "FROM biz_order o LEFT JOIN biz_demand d ON o.demand_id = d.id " +
            "LEFT JOIN biz_material m ON o.material_id = m.id LEFT JOIN sys_user u ON o.donor_id = u.id " +
            "WHERE o.recipient_id = #{recipientId} ORDER BY o.create_time DESC")
    List<Map<String, Object>> getRecipientOrders(@Param("recipientId") Long recipientId);

    // 【新增】捐助者的联系人列表（去重）
    @Select("SELECT DISTINCT u.id as contact_id, u.phone as contact_phone " +
            "FROM biz_order o LEFT JOIN sys_user u ON o.recipient_id = u.id " +
            "WHERE o.donor_id = #{donorId}")
    List<Map<String, Object>> getDonorContacts(@Param("donorId") Long donorId);

    // 【新增】受助方的联系人列表（去重）
    @Select("SELECT DISTINCT u.id as contact_id, u.phone as contact_phone " +
            "FROM biz_order o LEFT JOIN sys_user u ON o.donor_id = u.id " +
            "WHERE o.recipient_id = #{recipientId}")
    List<Map<String, Object>> getRecipientContacts(@Param("recipientId") Long recipientId);

    // 【新增】两人之间的所有捐赠记录
    @Select("SELECT o.id as order_id, o.create_time, o.logistics_status, " +
            "d.title as demand_title, m.name as material_name, m.images " +
            "FROM biz_order o LEFT JOIN biz_demand d ON o.demand_id = d.id " +
            "LEFT JOIN biz_material m ON o.material_id = m.id " +
            "WHERE o.donor_id = #{donorId} AND o.recipient_id = #{recipientId} ORDER BY o.create_time DESC")
    List<Map<String, Object>> getOrdersBetween(@Param("donorId") Long donorId, @Param("recipientId") Long recipientId);
    // 新增：获取平台最新8条匹配动态
    @Select("SELECT o.create_time, m.name as material_name, d.title as demand_title, u.phone as donor_phone " +
            "FROM biz_order o " +
            "LEFT JOIN biz_material m ON o.material_id = m.id " +
            "LEFT JOIN biz_demand d ON o.demand_id = d.id " +
            "LEFT JOIN sys_user u ON o.donor_id = u.id " +
            "ORDER BY o.create_time DESC LIMIT 8")
    List<Map<String, Object>> getLatestDynamics();
}