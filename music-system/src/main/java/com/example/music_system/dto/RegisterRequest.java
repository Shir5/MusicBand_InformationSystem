package com.example.music_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов.")
    private String password;

    @NotNull(message = "Поле isAdminRequest не может быть null.")
    private Boolean isAdminRequest = false; // Флаг, указывающий, что пользователь хочет стать администратором

    // Getters и Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdminRequest() {
        return isAdminRequest;
    }

    public void setIsAdminRequest(Boolean isAdminRequest) {
        this.isAdminRequest = isAdminRequest;
    }
}
