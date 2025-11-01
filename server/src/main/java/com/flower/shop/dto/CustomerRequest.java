package com.flower.shop.dto;

import jakarta.validation.constraints.*;

public class CustomerRequest {
    @NotBlank
    @Size(max = 32)
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\d{11}$", message = "手机号需为11位数字")
    private String phone;

    @Size(max = 2)
    private String sex;

    @Size(max = 500)
    private String avatar;

    @Min(0)
    @Max(2)
    private Integer type; // 0普通 1管理员 2编辑

    // 可选：仅在新增或主动修改时提供，后台会加密存储
    @Size(min = 6, max = 64)
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}