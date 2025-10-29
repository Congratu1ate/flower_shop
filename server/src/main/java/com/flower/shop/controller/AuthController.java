package com.flower.shop.controller;

import com.flower.shop.dto.LoginRequest;
import com.flower.shop.dto.RegisterRequest;
import com.flower.shop.dto.LoginResponse;
import com.flower.shop.dto.UserResponse;
import com.flower.shop.entity.User;
import com.flower.shop.service.UserService;
import com.flower.shop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final String COOKIE_NAME = "AUTH_TOKEN";

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User existing = userService.findByPhone(req.getPhone());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("PHONE_EXISTS");
        }

        User u = new User();
        u.setType(0); // 普通用户
        u.setName(req.getName());
        u.setPhone(req.getPhone());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setSex(req.getSex());
        u.setAvatar(req.getAvatar());

        userService.registerUser(u);

        UserResponse resp = new UserResponse(u.getId(), u.getName(), u.getType(), u.getPhone(), u.getAvatar());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.findByPhone(req.getPhone());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_CREDENTIALS");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getType(), user.getName(), user.getPhone());
        long maxAge = (req.getRememberMe() != null && req.getRememberMe()) ? jwtUtil.getExpireSeconds() : jwtUtil.getExpireSeconds();

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();

        UserResponse respUser = new UserResponse(user.getId(), user.getName(), user.getType(), user.getPhone(), user.getAvatar());
        LoginResponse resp = new LoginResponse(token, jwtUtil.getExpireSeconds(), respUser);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if (COOKIE_NAME.equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long uid = ((Number) claims.get("uid")).longValue();
            User user = userService.findById(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
            }
            UserResponse respUser = new UserResponse(user.getId(), user.getName(), user.getType(), user.getPhone(), user.getAvatar());
            return ResponseEntity.ok(respUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_TOKEN");
        }
    }
}