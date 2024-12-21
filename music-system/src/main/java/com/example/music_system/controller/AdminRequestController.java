package com.example.music_system.controller;

import com.example.music_system.model.AdminRequest;
import com.example.music_system.model.User;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/requests")
public class AdminRequestController {

    private final UserService userService;

    private final JwtUtil jwtUtil;
    @Autowired
    public AdminRequestController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping
    public List<AdminRequest> getPendingRequests(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Убираем "Bearer "
        User user = userService.findUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isAdmin()) {
            throw new RuntimeException("Access denied");
        }

        return userService.getPendingAdminRequests();
    }

    @PostMapping("/{id}/approve")
    public void approveRequest(@PathVariable Integer id) {
        userService.processAdminRequest(id, true);
    }

    @PostMapping("/{id}/reject")
    public void rejectRequest(@PathVariable Integer id) {
        userService.processAdminRequest(id, false);
    }
}
