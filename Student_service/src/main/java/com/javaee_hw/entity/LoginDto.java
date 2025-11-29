package com.javaee_hw.entity;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String code; // 验证码
    private String uuid; // 验证码对应的key
}