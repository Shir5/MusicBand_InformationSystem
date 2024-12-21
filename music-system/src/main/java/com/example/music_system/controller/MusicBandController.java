package com.example.music_system.controller;

import com.example.music_system.dto.GroupByCreationDateDto;
import com.example.music_system.dto.MusicBandDto;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.MusicBandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
public class MusicBandController {

    private final MusicBandService musicBandService;
    private final JwtUtil jwtUtil;

    public MusicBandController(MusicBandService musicBandService, JwtUtil jwtUtil) {
        this.musicBandService = musicBandService;
        this.jwtUtil = jwtUtil;
    }

    // Получение всех музыкальных групп с фильтром и пагинацией
    @GetMapping
    public Page<MusicBandDto> getAllBands(
            @RequestParam(defaultValue = "") String filter,
            Pageable pageable
    ) {
        return musicBandService.getAllBands(filter, pageable);
    }

    // Получение группы по ID
    @GetMapping("/{id}")
    public ResponseEntity<MusicBandDto> getBandById(@PathVariable Integer id) {
        return musicBandService.getBandById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Band not found with ID: " + id));
    }

    // Создание новой группы
    @PostMapping
    public ResponseEntity<MusicBandDto> createBand(
            @RequestBody MusicBandDto bandDto,
            @RequestHeader("Authorization") String token
    ) {
        String username = extractUsernameFromToken(token);
        MusicBandDto createdBand = musicBandService.createBand(bandDto, username);
        return ResponseEntity.ok(createdBand);
    }

    // Обновление группы
    @PutMapping("/{id}")
    public ResponseEntity<MusicBandDto> updateBand(
            @PathVariable Integer id,
            @RequestBody MusicBandDto bandDto,
            @RequestHeader("Authorization") String token
    ) {
        String username = extractUsernameFromToken(token);
        MusicBandDto updatedBand = musicBandService.updateBand(id, bandDto, username);
        return ResponseEntity.ok(updatedBand);
    }

    // Удаление группы
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token
    ) {
        String username = extractUsernameFromToken(token);
        musicBandService.deleteBand(id, username);
        return ResponseEntity.noContent().build();
    }


    // Извлечение имени пользователя из токена
    private String extractUsernameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return jwtUtil.extractUsername(token.substring(7));
        }
        throw new RuntimeException("Invalid Authorization header format");
    }
}
