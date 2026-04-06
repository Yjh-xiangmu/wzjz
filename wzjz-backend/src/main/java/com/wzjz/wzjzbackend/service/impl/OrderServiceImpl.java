package com.wzjz.wzjzbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzjz.wzjzbackend.entity.Demand;
import com.wzjz.wzjzbackend.entity.Material;
import com.wzjz.wzjzbackend.entity.Order;
import com.wzjz.wzjzbackend.mapper.OrderMapper;
import com.wzjz.wzjzbackend.service.DemandService;
import com.wzjz.wzjzbackend.service.MaterialService;
import com.wzjz.wzjzbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private DemandService demandService; // 注入需求Service

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDonateOrder(Order order) {
        // 1. 保存订单
        order.setLogisticsStatus(0);
        this.save(order);

        // 2. 更新物资状态为“已匹配”
        Material material = materialService.getById(order.getMaterialId());
        material.setStatus(1);
        materialService.updateById(material);

        // 3. 更新需求表的“已筹集数量”
        Demand demand = demandService.getById(order.getDemandId());
        int currentMatched = demand.getMatchedQuantity() == null ? 0 : demand.getMatchedQuantity();
        // 加上这次捐赠的物资数量
        int newMatched = currentMatched + material.getQuantity();
        demand.setMatchedQuantity(newMatched);

        // 4. 如果筹集够了（甚至超了），就把需求状态改成 2(已完成)，这样大厅里就不会再显示了
        if (newMatched >= demand.getQuantity()) {
            demand.setStatus(2);
        }

        demandService.updateById(demand);
    }
}