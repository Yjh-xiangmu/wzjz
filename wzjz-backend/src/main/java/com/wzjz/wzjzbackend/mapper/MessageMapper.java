package com.wzjz.wzjzbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzjz.wzjzbackend.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}