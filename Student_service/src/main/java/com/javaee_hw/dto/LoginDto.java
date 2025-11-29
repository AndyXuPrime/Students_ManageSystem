package com.javaee_hw.dto; // 必须是这个包名

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String code; // 验证码
    private String uuid; // 验证码对应的key
}