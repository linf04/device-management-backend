package com.device.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ログインレスポンス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String token;
    private UserDto userDTO;
}
