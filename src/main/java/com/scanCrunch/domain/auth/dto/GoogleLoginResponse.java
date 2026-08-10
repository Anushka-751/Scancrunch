package com.scanCrunch.domain.auth.dto;

public class GoogleLoginResponse {

    private String token;
    private AuthResponse user;

    public GoogleLoginResponse() {
    }

    public GoogleLoginResponse(String token, AuthResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthResponse getUser() {
        return user;
    }

    public void setUser(AuthResponse user) {
        this.user = user;
    }
}