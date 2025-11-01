package com.flower.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.CustomerRequest;
import com.flower.shop.dto.CustomerResponse;
import com.flower.shop.entity.User;
import com.flower.shop.mapper.UserMapper;
import com.flower.shop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User registerUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public Page<CustomerResponse> list(String q, Integer type, long page, long size) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (q != null && !q.isBlank()) {
            String k = q.trim();
            qw.and(wrapper -> wrapper.like(User::getName, k).or().like(User::getPhone, k));
        }
        if (type != null) qw.eq(User::getType, type);
        qw.orderByDesc(User::getUpdateTime).orderByDesc(User::getCreateTime);
        Page<User> p = new Page<>(page, size);
        Page<User> users = userMapper.selectPage(p, qw);

        Page<CustomerResponse> resp = new Page<>(users.getCurrent(), users.getSize(), users.getTotal());
        resp.setRecords(users.getRecords().stream().map(u -> new CustomerResponse(
                u.getId(), u.getName(), u.getPhone(), u.getType(), u.getAvatar(), u.getCreateTime()
        )).toList());
        return resp;
    }

    @Override
    public User create(CustomerRequest req) {
        // 唯一性校验：手机号
        if (req.getPhone() != null) {
            User exists = findByPhone(req.getPhone());
            if (exists != null) return null; // 由控制器返回 409
        }
        User u = new User();
        u.setType(req.getType() == null ? 0 : req.getType());
        u.setName(req.getName());
        u.setPhone(req.getPhone());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        u.setSex(req.getSex());
        u.setAvatar(req.getAvatar());
        // 无 email/status 字段
        userMapper.insert(u);
        return u;
    }

    @Override
    public User update(Long id, CustomerRequest req) {
        User u = userMapper.selectById(id);
        if (u == null) return null;
        if (req.getName() != null) u.setName(req.getName());
        if (req.getPhone() != null && !req.getPhone().equals(u.getPhone())) {
            User exists = findByPhone(req.getPhone());
            if (exists != null && !exists.getId().equals(id)) return null; // 手机冲突：返回 null，由控制器决定 409
            u.setPhone(req.getPhone());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getSex() != null) u.setSex(req.getSex());
        if (req.getAvatar() != null) u.setAvatar(req.getAvatar());
        if (req.getType() != null) u.setType(req.getType());
        userMapper.updateById(u);
        return u;
    }

    @Override
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}