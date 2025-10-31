package com.flower.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.FlowerRequest;
import com.flower.shop.entity.Flower;
import com.flower.shop.mapper.FlowerMapper;
import com.flower.shop.service.FlowerService;
import org.springframework.stereotype.Service;

@Service
public class FlowerServiceImpl implements FlowerService {
    private final FlowerMapper flowerMapper;

    public FlowerServiceImpl(FlowerMapper flowerMapper) {
        this.flowerMapper = flowerMapper;
    }

    @Override
    public Page<Flower> list(String q, Long categoryId, Integer status, long page, long size) {
        LambdaQueryWrapper<Flower> qw = new LambdaQueryWrapper<>();
        if (q != null && !q.isBlank()) qw.like(Flower::getName, q.trim());
        if (categoryId != null) qw.eq(Flower::getCategoryId, categoryId);
        if (status != null) qw.eq(Flower::getStatus, status);
        qw.orderByDesc(Flower::getUpdateTime).orderByDesc(Flower::getCreateTime);
        Page<Flower> p = new Page<>(page, size);
        return flowerMapper.selectPage(p, qw);
    }

    @Override
    public Flower create(FlowerRequest req) {
        Flower f = new Flower();
        f.setName(req.getName().trim());
        f.setCategoryId(req.getCategoryId());
        f.setPrice(req.getPrice());
        f.setImage(req.getImage());
        f.setDescription(req.getDescription());
        f.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        f.setInventory(req.getInventory() == null ? 0 : req.getInventory());
        flowerMapper.insert(f);
        return f;
    }

    @Override
    public Flower update(Long id, FlowerRequest req) {
        Flower f = flowerMapper.selectById(id);
        if (f == null) return null;
        f.setName(req.getName().trim());
        f.setCategoryId(req.getCategoryId());
        f.setPrice(req.getPrice());
        f.setImage(req.getImage());
        f.setDescription(req.getDescription());
        if (req.getStatus() != null) f.setStatus(req.getStatus());
        if (req.getInventory() != null) f.setInventory(req.getInventory());
        flowerMapper.updateById(f);
        return f;
    }

    @Override
    public boolean delete(Long id) {
        return flowerMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Flower f = flowerMapper.selectById(id);
        if (f == null) return false;
        f.setStatus(status);
        return flowerMapper.updateById(f) > 0;
    }

    @Override
    public Flower findById(Long id) {
        return flowerMapper.selectById(id);
    }
}