package com.flower.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.OrderListItemResponse;
import com.flower.shop.entity.Order;
import com.flower.shop.entity.OrderDetail;
import com.flower.shop.entity.User;
import com.flower.shop.mapper.OrderDetailMapper;
import com.flower.shop.mapper.OrderMapper;
import com.flower.shop.mapper.UserMapper;
import com.flower.shop.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserMapper userMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Page<OrderListItemResponse> list(String q, Integer status, LocalDate startDate, LocalDate endDate, long page, long size) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();
        if (status != null) qw.eq(Order::getStatus, status);
        if (startDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            qw.ge(Order::getOrderTime, start);
        }
        if (endDate != null) {
            LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusSeconds(1);
            qw.le(Order::getOrderTime, end);
        }

        // q 支持订单号精确/模糊、或用户姓名/手机号模糊
        Set<Long> userIds = new HashSet<>();
        if (q != null && !q.isBlank()) {
            String s = q.trim();
            // 订单号：去掉可能的 # 前缀
            String normalized = s.startsWith("#") ? s.substring(1) : s;
            qw.like(Order::getNumber, normalized);

            // 同时尝试匹配用户
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .like(User::getName, s)
                    .or()
                    .like(User::getPhone, s));
            for (User u : users) userIds.add(u.getId());
        }
        if (!userIds.isEmpty()) {
            qw.in(Order::getUserId, userIds);
        }

        qw.orderByDesc(Order::getOrderTime).orderByDesc(Order::getId);
        Page<Order> p = new Page<>(page, size);
        Page<Order> orders = orderMapper.selectPage(p, qw);

        // 批量查询用户信息，构造响应（确保在lambda中捕获的是最终变量）
        final Map<Long, User> usersMap;
        if (!orders.getRecords().isEmpty()) {
            Set<Long> uids = orders.getRecords().stream().map(Order::getUserId).collect(Collectors.toSet());
            if (!uids.isEmpty()) {
                List<User> userList = userMapper.selectBatchIds(uids);
                usersMap = userList.stream().collect(Collectors.toMap(User::getId, x -> x));
            } else {
                usersMap = Collections.emptyMap();
            }
        } else {
            usersMap = Collections.emptyMap();
        }

        Page<OrderListItemResponse> resp = new Page<>(orders.getCurrent(), orders.getSize(), orders.getTotal());
        resp.setRecords(orders.getRecords().stream().map(o -> {
            User u = usersMap.get(o.getUserId());
            String name = u != null ? u.getName() : "";
            String phone = u != null ? u.getPhone() : "";
            return new OrderListItemResponse(o.getId(), o.getNumber(), o.getAmount(), o.getStatus(), name, phone, o.getOrderTime());
        }).collect(Collectors.toList()));
        return resp;
    }

    @Override
    public Order findById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<OrderDetail> listDetails(Long orderId) {
        return orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orderId));
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Order o = orderMapper.selectById(id);
        if (o == null) return false;
        // 简单的状态流转约束（可按需扩展）：
        // 1待付款->2已付款；2已付款->3配送中；3配送中->4已完成；任意->5取消；2->6退款
        Integer from = o.getStatus();
        if (!isAllowedTransition(from, status)) return false;
        o.setStatus(status);
        if (status != null && status == 2 && o.getCheckoutTime() == null) {
            o.setCheckoutTime(LocalDateTime.now());
        }
        return orderMapper.updateById(o) > 0;
    }

    private boolean isAllowedTransition(Integer from, Integer to) {
        if (from == null || to == null) return false;
        if (Objects.equals(from, to)) return true;
        return switch (from) {
            case 1 -> (to == 2 || to == 5); // 待付款→已付款/取消
            case 2 -> (to == 3 || to == 6 || to == 5); // 已付款→配送中/退款/取消
            case 3 -> (to == 4 || to == 5); // 配送中→已完成/取消
            case 4 -> false; // 已完成不可变
            case 5 -> false; // 取消不可变
            case 6 -> false; // 退款不可变
            default -> false;
        };
    }

    @Override
    public boolean updateRemark(Long id, String remark) {
        Order o = orderMapper.selectById(id);
        if (o == null) return false;
        o.setRemark(remark);
        return orderMapper.updateById(o) > 0;
    }
}