package com.flower.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flower.shop.dto.FlowerRequest;
import com.flower.shop.dto.FlowerResponse;
import com.flower.shop.entity.Flower;
import com.flower.shop.service.FlowerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flowers")
public class FlowerController {
    private final FlowerService flowerService;

    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        Page<Flower> p = flowerService.list(q, categoryId, status, page, size);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        Flower f = flowerService.findById(id);
        if (f == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        return ResponseEntity.ok(toResp(f));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FlowerRequest req) {
        Flower f = flowerService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResp(f));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody FlowerRequest req) {
        Flower f = flowerService.update(id, req);
        if (f == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        return ResponseEntity.ok(toResp(f));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        boolean ok = flowerService.updateStatus(id, status);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return flowerService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private FlowerResponse toResp(Flower f) {
        return new FlowerResponse(
                f.getId(), f.getName(), f.getCategoryId(), f.getPrice(), f.getImage(),
                f.getDescription(), f.getStatus(), f.getInventory()
        );
    }
}