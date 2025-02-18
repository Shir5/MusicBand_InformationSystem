package com.example.music_system.service;

import com.example.music_system.model.ImportHistory;
import com.example.music_system.repository.ImportHistoryRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ImportHistoryService(ImportHistoryRepository importHistoryRepository, SimpMessagingTemplate messagingTemplate) {
        this.importHistoryRepository = importHistoryRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Метод для получения истории пользователя
    public List<ImportHistory> getHistoryForUser(String username) {
        List<ImportHistory> history = importHistoryRepository.findByUsername(username);

        // Уведомление через WebSocket
        messagingTemplate.convertAndSend("/topic/history", Map.of(
                "action", "user-history",
                "username", username,
                "message", "User history retrieved",
                "data", history
        ));

        return history;
    }

    // Метод для получения всей истории (для администратора)
    public List<ImportHistory> getAllHistory() {
        List<ImportHistory> history = importHistoryRepository.findAllOperations();

        // Уведомление через WebSocket
        messagingTemplate.convertAndSend("/topic/history", Map.of(
                "action", "all-history",
                "message", "All history retrieved",
                "data", history
        ));

        return history;
    }

    // Новый метод для получения ImportHistory по ID
    public ImportHistory getImportHistoryById(Integer id) {
        return importHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ImportHistory not found with ID: " + id));
    }
}
