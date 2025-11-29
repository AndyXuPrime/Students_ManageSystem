package com.javaee_hw.controller;

import com.sims.common.Result;
import com.sims.dto.LoginDto;
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
        // 存入Redis，2分钟过期
        redisTemplate.opsForValue().set("captcha:" + uuid, code, 2, TimeUnit.MINUTES);
        
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("code", code); // 实际项目应生成图片流，这里直接返回code供实验测试
        return Result.success(map);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto dto) {
        String key = "captcha:" + dto.getUuid();
        String realCode = redisTemplate.opsForValue().get(key);
        
        if (realCode == null) return Result.error("验证码已过期");
        if (!realCode.equals(dto.getCode())) return Result.error("验证码错误");
        
        redisTemplate.delete(key); // 验证成功后删除
        
        // TODO: 这里应查询 sys_user 表校验用户名密码
        if ("admin".equals(dto.getUsername()) && "123456".equals(dto.getPassword())) {
             return Result.success("fake-jwt-token-xxxxx");
        }
        return Result.error("用户名或密码错误");
    }
}