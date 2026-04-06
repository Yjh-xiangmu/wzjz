package com.wzjz.wzjzbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzjz.wzjzbackend.entity.Demand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DemandMapper extends BaseMapper<Demand> {

    // 管理员：查询需求列表及对应的受助方信息
    @Select("SELECT d.*, u.phone as recipient_phone, u.org_name " +
            "FROM biz_demand d " +
            "LEFT JOIN sys_user u ON d.recipient_id = u.id " +
            "WHERE (#{status} IS NULL OR d.status = #{status}) " +
            "ORDER BY d.create_time DESC")
    List<Map<String, Object>> getAdminDemandList(@Param("status") Integer status);
}