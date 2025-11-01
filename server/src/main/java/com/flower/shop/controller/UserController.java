package com.flower.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.CustomerRequest;
import com.flower.shop.dto.CustomerResponse;
import com.flower.shop.entity.User;
import com.flower.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        Page<CustomerResponse> p = userService.list(q, type, page, size);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        User u = userService.findById(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        CustomerResponse resp = new CustomerResponse(u.getId(), u.getName(), u.getPhone(), u.getType(), u.getAvatar(), u.getCreateTime());
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CustomerRequest req) {
        User u = userService.create(req);
        if (u == null) return ResponseEntity.status(HttpStatus.CONFLICT).body("PHONE_EXISTS");
        CustomerResponse resp = new CustomerResponse(u.getId(), u.getName(), u.getPhone(), u.getType(), u.getAvatar(), u.getCreateTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody CustomerRequest req) {
        User u = userService.update(id, req);
        if (u == null) {
            // 判断是不存在还是手机号冲突：这里统一使用 404/409 的简化处理
            User exists = userService.findById(id);
            if (exists == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("PHONE_EXISTS");
        }
        CustomerResponse resp = new CustomerResponse(u.getId(), u.getName(), u.getPhone(), u.getType(), u.getAvatar(), u.getCreateTime());
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return userService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}