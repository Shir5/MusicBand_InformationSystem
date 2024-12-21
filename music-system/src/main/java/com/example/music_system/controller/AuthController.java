package com.example.music_system.controller;

import com.example.music_system.dto.RegisterRequest;
import com.example.music_system.model.User;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "http://localhost:3000", // Указываем точный источник фронтенда
        allowCredentials = "true", // Включаем отправку credentials (например, cookies)
        allowedHeaders = "*", // Разрешаем все заголовки
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS} // Разрешаем методы
)
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username or password is missing"));
        }

        // Проверка формата username
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username can only contain Latin letters, digits, and underscores"));
        }

        try {
            // Проверка username и пароля через сервис
            User user = userService.authenticateUser(username, password);

            // Генерация токена
            String token = jwtUtil.generateToken(username);

            // Получение роли пользователя
            String role = user.getRole().toString();

            // Возвращаем токен и роль в ответе
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", role
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username or password is missing"));
        }

        // Проверка формата username
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username can only contain Latin letters, digits, and underscores"));
        }

        try {
            // Регистрация нового пользователя через сервис
            userService.registerUser(registerRequest, 8);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        // Генерация токена для нового пользователя
        String token = jwtUtil.generateToken(registerRequest.getUsername());

        // Извлечение роли зарегистрированного пользователя
        String role = userService.findUserByUsername(registerRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after registration"))
                .getRole().toString();

        // Возвращаем токен и роль в ответе
        return ResponseEntity.status(201).body(Map.of(
                "token", token,
                "role", role
        ));
    }

}
