package com.wzjz.wzjzbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzjz.wzjzbackend.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    // 管理员：查询所有物资列表及对应的捐助者账号
    @Select("SELECT m.*, u.phone as donor_phone " +
            "FROM biz_material m " +
            "LEFT JOIN sys_user u ON m.donor_id = u.id " +
            "WHERE (#{status} IS NULL OR m.status = #{status}) " +
            "ORDER BY m.create_time DESC")
    List<Map<String, Object>> getAdminMaterialList(@Param("status") Integer status);
}