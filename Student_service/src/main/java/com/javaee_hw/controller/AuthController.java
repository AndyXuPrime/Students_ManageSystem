package com.javaee_hw.controller;

import com.javaee_hw.common.Result;   // 修正引用
import com.javaee_hw.dto.LoginDto;    // 修正引用
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 1000));
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("captcha:" + uuid, code, 2, TimeUnit.MINUTES);
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("code", code);
        return Result.success(map);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto dto) {
        String key = "captcha:" + dto.getUuid();
        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode == null) return Result.error("验证码已过期");
        if (!realCode.equals(dto.getCode())) return Result.error("验证码错误");
        redisTemplate.delete(key);
        return Result.success("fake-jwt-token-xxxxx");
    }
}