package com.example.music_system.controller;

import com.example.music_system.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/group-by-creation-date")
    public List<Map<String, Object>> groupByCreationDate() {
        return databaseService.groupByCreationDate();
    }

    // Подсчет лейблов
    @GetMapping("/count-labels")
    public int countLabels(@RequestParam long threshold) {
        return databaseService.countLabelsWithBandsAbove(threshold);
    }

    // Поиск описаний по префиксу
    @GetMapping("/find-descriptions")
    public List<Map<String, Object>> findDescriptions(@RequestParam String prefix) {
        return databaseService.findDescriptionsByPrefix(prefix);
    }

    // Добавление сингла
    @PostMapping("/add-single")
    public void addSingle(@RequestParam int bandId, @RequestParam int singlesToAdd) {
        databaseService.addSingleToBand(bandId, singlesToAdd);
    }

    // Добавление участника
    @PostMapping("/add-participant")
    public void addParticipant(@RequestParam int bandId, @RequestParam int participantsToAdd) {
        databaseService.addParticipantToBand(bandId, participantsToAdd);
    }
}
