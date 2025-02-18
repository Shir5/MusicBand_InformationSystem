package com.example.music_system.controller;

import com.example.music_system.model.ImportHistory;
import com.example.music_system.security.JwtUtil;
import com.example.music_system.service.ImportHistoryService;
import com.example.music_system.service.MinioFileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class ImportHistoryController {

    private final ImportHistoryService importHistoryService;
    private final JwtUtil jwtUtil;
    private final MinioFileService minioFileService;

    public ImportHistoryController(ImportHistoryService importHistoryService, JwtUtil jwtUtil, MinioFileService minioFileService) {
        this.importHistoryService = importHistoryService;
        this.jwtUtil = jwtUtil;
        this.minioFileService = minioFileService;
    }

    @GetMapping
    public ResponseEntity<List<ImportHistory>> getImportHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        String username = jwtUtil.extractUsernameFromToken(token);

        List<ImportHistory> history;
        if (isAdmin) {
            history = importHistoryService.getAllHistory();
        } else {
            history = importHistoryService.getHistoryForUser(username);
        }

        return ResponseEntity.ok(history);
    }

    /**
     * Эндпоинт для скачивания загруженного файла по ID ImportHistory
     */
    @GetMapping("/{id}/download-file")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("id") Integer importHistoryId) {
        // 1. Находим запись об импорте
        ImportHistory history = importHistoryService.getImportHistoryById(importHistoryId);

        // 2. Проверяем, есть ли данные о файле
        String objectName = history.getFileObjectName();
        if (objectName == null || objectName.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
            // Или выбросить исключение, которое будет обработано глобальным обработчиком
            // throw new RuntimeException("No file was attached for this import.");
        }

        // Получаем исходное имя файла
        String fileName = history.getFileName();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "imported_file";
        }

        // 3. Скачиваем файл из MinIO
        InputStream inputStream = minioFileService.downloadFile(objectName);

        // 4. Упаковываем в InputStreamResource
        InputStreamResource resource = new InputStreamResource(inputStream);

        // 5. Возвращаем ResponseEntity с заголовками для скачивания
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
