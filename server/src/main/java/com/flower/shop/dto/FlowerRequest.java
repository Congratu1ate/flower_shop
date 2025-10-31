package com.flower.shop.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class FlowerRequest {
    @NotBlank(message = "name 不能为空")
    @Size(max = 32, message = "name 最长 32 字符")
    private String name;

    @NotNull(message = "categoryId 不能为空")
    private Long categoryId;

    @NotNull(message = "price 不能为空")
    @DecimalMin(value = "0.00", message = "price 不能为负数")
    private BigDecimal price;

    private String image;

    @Size(max = 255, message = "description 最长 255 字符")
    private String description;

    @Min(0) @Max(1)
    private Integer status; // 1 在售，0 下架

    @Min(0)
    private Integer inventory; // 方案A：库存

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