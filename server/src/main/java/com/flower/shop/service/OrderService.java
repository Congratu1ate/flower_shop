package com.flower.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.OrderListItemResponse;
import com.flower.shop.entity.Order;
import com.flower.shop.entity.OrderDetail;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Page<OrderListItemResponse> list(String q, Integer status, LocalDate startDate, LocalDate endDate, long page, long size);
    Order findById(Long id);
    List<OrderDetail> listDetails(Long orderId);
    boolean updateStatus(Long id, Integer status);
    boolean updateRemark(Long id, String remark);
}