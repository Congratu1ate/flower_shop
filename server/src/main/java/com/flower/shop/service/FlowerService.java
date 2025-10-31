package com.flower.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.FlowerRequest;
import com.flower.shop.entity.Flower;

public interface FlowerService {
    Page<Flower> list(String q, Long categoryId, Integer status, long page, long size);
    Flower create(FlowerRequest req);
    Flower update(Long id, FlowerRequest req);
    boolean delete(Long id);
    boolean updateStatus(Long id, Integer status);
    Flower findById(Long id);
}