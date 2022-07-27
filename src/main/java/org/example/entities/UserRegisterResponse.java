package org.example.entities;

import lombok.Data;

@Data
public class UserRegisterResponse {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
