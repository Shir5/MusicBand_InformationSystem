package com.example.music_system.controller;

import com.example.music_system.model.Album;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.AlbumService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final JwtUtil jwtUtil; // Для работы с JWT токенами

    public AlbumController(AlbumService albumService, JwtUtil jwtUtil) {
        this.albumService = albumService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Album> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public Album getAlbumById(@PathVariable Integer id) {
        return albumService.getAlbumById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with ID: " + id));
    }

    @PostMapping
    public Album createAlbum(@RequestBody Album album, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        return albumService.createAlbum(album, username); // Передаем username в сервис
    }

    @PutMapping("/{id}")
    public Album updateAlbum(@PathVariable Integer id, @RequestBody Album album, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        return albumService.updateAlbum(id, album, username); // Передаем username в сервис
    }

    @DeleteMapping("/{id}")
    public void deleteAlbum(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        albumService.deleteAlbum(id, username); // Передаем username в сервис
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
