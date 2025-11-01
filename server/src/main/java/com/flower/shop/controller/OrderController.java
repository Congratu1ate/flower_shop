package com.flower.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.OrderListItemResponse;
import com.flower.shop.entity.Order;
import com.flower.shop.entity.OrderDetail;
import com.flower.shop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        LocalDate startDate = startDateStr == null || startDateStr.isBlank() ? null : LocalDate.parse(startDateStr);
        LocalDate endDate = endDateStr == null || endDateStr.isBlank() ? null : LocalDate.parse(endDateStr);
        Page<OrderListItemResponse> p = orderService.list(q, status, startDate, endDate, page, size);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        Order o = orderService.findById(id);
        if (o == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        List<OrderDetail> items = orderService.listDetails(id);
        Map<String, Object> body = new HashMap<>();
        body.put("order", o);
        body.put("items", items);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        boolean ok = orderService.updateStatus(id, status);
        if (!ok) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_STATUS_TRANSITION");
        return ResponseEntity.ok("OK");
    }

    @PatchMapping("/{id}/remark")
    public ResponseEntity<?> updateRemark(@PathVariable("id") Long id, @RequestBody Map<String, String> req) {
        String remark = req == null ? null : req.get("remark");
        boolean ok = orderService.updateRemark(id, remark);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        return ResponseEntity.ok("OK");
    }
}