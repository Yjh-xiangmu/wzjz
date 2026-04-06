package com.wzjz.wzjzbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzjz.wzjzbackend.entity.Order;

public interface OrderService extends IService<Order> {
    void createDonateOrder(Order order);
}