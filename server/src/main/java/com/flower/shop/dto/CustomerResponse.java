package com.flower.shop.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

public class CustomerResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String phone;
    private Integer type;   // 0普通 1管理员 2编辑
    private String avatar;
    private LocalDateTime createTime;

    public CustomerResponse(Long id, String name, String phone, Integer type, String avatar, LocalDateTime createTime) {
        this.id = id; this.name = name; this.phone = phone; this.type = type; this.avatar = avatar; this.createTime = createTime;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public Integer getType() { return type; }
    public String getAvatar() { return avatar; }
    public LocalDateTime getCreateTime() { return createTime; }
}