package com.flower.shop.dto;

public class UserResponse {
    private Long id;
    private String name;
    private Integer type;
    private String phone;
    private String avatar;

    public UserResponse() {}

    public UserResponse(Long id, String name, Integer type, String phone, String avatar) {
        this.id = id; this.name = name; this.type = type; this.phone = phone; this.avatar = avatar;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}