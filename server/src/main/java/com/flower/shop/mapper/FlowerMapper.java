package com.flower.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flower.shop.entity.Flower;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowerMapper extends BaseMapper<Flower> {
}