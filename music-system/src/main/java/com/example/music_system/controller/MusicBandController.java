package com.example.music_system.controller;

import com.example.music_system.dto.*;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.MusicBandService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
        String username = jwtUtil.extractUsernameFromToken(token);
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
        String username = jwtUtil.extractUsernameFromToken(token);
        MusicBandDto updatedBand = musicBandService.updateBand(id, bandDto, username);
        return ResponseEntity.ok(updatedBand);
    }

    // Удаление группы
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token
    ) {
        String username = jwtUtil.extractUsernameFromToken(token);
        musicBandService.deleteBand(id, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import-bands", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importBandsWithEntities(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") String jsonData,
            @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsernameFromToken(token);

        try {
            // Создаём ObjectMapper с поддержкой JavaTimeModule
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Формат ISO 8601

            // Десериализуем строку JSON в список DTO
            List<MusicBandImportDto> importDtos = objectMapper.readValue(
                    jsonData, new TypeReference<List<MusicBandImportDto>>() {});

            // Передаём список объектов в сервис
            musicBandService.importBandsWithEntities(importDtos, username, file);

            return ResponseEntity.ok("Bands and related entities imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}

