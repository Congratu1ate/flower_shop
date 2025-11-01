package com.flower.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件为空");
        }

        // 简单的类型校验，只允许图片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("仅支持图片上传");
        }

        // 目标目录：应用启动目录下的 uploads（或自定义）
        Path root = Paths.get("").toAbsolutePath();
        Path dir = root.resolve(uploadDir);
        Files.createDirectories(dir);

        // 生成安全文件名：uuid + 原扩展名
        String original = file.getOriginalFilename();
        String ext = "";
        if (StringUtils.hasText(original) && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;

        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        // 返回可访问的 URL（由 WebMvcConfig 映射）
        String url = "/uploads/" + filename;
        Map<String, Object> body = new HashMap<>();
        body.put("url", url);
        body.put("name", filename);
        body.put("size", file.getSize());
        body.put("type", contentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}