package com.flower.shop.service;

import com.flower.shop.entity.User;

public interface UserService {
    User findByPhone(String phone);
    User findById(Long id);
    User registerUser(User user);
}