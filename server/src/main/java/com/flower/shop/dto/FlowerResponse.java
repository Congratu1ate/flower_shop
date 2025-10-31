package com.flower.shop.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class FlowerResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
    private Integer inventory;

    public FlowerResponse() {}

    public FlowerResponse(Long id, String name, Long categoryId, BigDecimal price, String image,
                          String description, Integer status, Integer inventory) {
        this.id = id; this.name = name; this.categoryId = categoryId; this.price = price;
        this.image = image; this.description = description; this.status = status; this.inventory = inventory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getInventory() { return inventory; }
    public void setInventory(Integer inventory) { this.inventory = inventory; }
}