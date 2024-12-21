package com.example.music_system.controller;

import com.example.music_system.model.Label;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.LabelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;
    private final JwtUtil jwtUtil; // Для извлечения информации из токена

    public LabelController(LabelService labelService, JwtUtil jwtUtil) {
        this.labelService = labelService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @GetMapping("/{id}")
    public Label getLabelById(@PathVariable Integer id) {
        return labelService.getLabelById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + id));
    }

    @PostMapping
    public Label createLabel(@RequestBody Label label, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        return labelService.createLabel(label, username);
    }

    @PutMapping("/{id}")
    public Label updateLabel(@PathVariable Integer id, @RequestBody Label label, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        return labelService.updateLabel(id, label, username);
    }

    @DeleteMapping("/{id}")
    public void deleteLabel(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        labelService.deleteLabel(id, username);
    }

    // Метод для извлечения имени пользователя из токена
    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Убираем "Bearer "
            return jwtUtil.extractUsername(token); // Извлекаем username через JwtUtil
        }
        throw new RuntimeException("Invalid Authorization header format");
    }
}
