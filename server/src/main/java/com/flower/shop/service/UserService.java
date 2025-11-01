package com.flower.shop.service;

import com.flower.shop.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.CustomerRequest;
import com.flower.shop.dto.CustomerResponse;

public interface UserService {
    User findByPhone(String phone);
    User findById(Long id);
    User registerUser(User user);

    // 客户管理
    Page<CustomerResponse> list(String q, Integer type, long page, long size);
    User create(CustomerRequest req);
    User update(Long id, CustomerRequest req);
    boolean delete(Long id);
}