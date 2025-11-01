package com.flower.shop.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderListItemResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String number;
    private BigDecimal amount;
    private Integer status;
    private String consigneeName; // 对应 user.name
    private String consigneePhone; // 对应 user.phone
    private LocalDateTime orderTime;

    public OrderListItemResponse() {}

    public OrderListItemResponse(Long id, String number, BigDecimal amount, Integer status,
                                 String consigneeName, String consigneePhone, LocalDateTime orderTime) {
        this.id = id; this.number = number; this.amount = amount; this.status = status;
        this.consigneeName = consigneeName; this.consigneePhone = consigneePhone; this.orderTime = orderTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getConsigneeName() { return consigneeName; }
    public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }
    public String getConsigneePhone() { return consigneePhone; }
    public void setConsigneePhone(String consigneePhone) { this.consigneePhone = consigneePhone; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
}