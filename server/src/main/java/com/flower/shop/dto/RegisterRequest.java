package com.flower.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 32, message = "姓名长度应为2-32个字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号需为11位数字")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码至少8位")
    private String password;

    private String sex; // 男/女/未知，可选
    private String avatar; // 可选

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}