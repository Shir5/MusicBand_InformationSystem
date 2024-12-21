package com.example.music_system.service;

import com.example.music_system.model.MusicBand;
import com.example.music_system.model.Role;
import com.example.music_system.model.User;
import com.example.music_system.repository.MusicBandRepository;
import com.example.music_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MusicBandRepository bandRepository;

    @Autowired
    private UserRepository userRepository;

    // Проверка, является ли текущий пользователь администратором или создателем группы
    private void validateUserPermission(int bandId) {
        // Получение текущего пользователя из контекста безопасности
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Получение группы и проверка прав
        MusicBand existingBand = bandRepository.findById(bandId).orElseThrow(() -> new RuntimeException("Band not found"));
        if (!existingBand.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to modify this band.");
        }
    }

    // Подсчет лейблов с количеством групп больше заданного
    public int countLabelsWithBandsAbove(long threshold) {
        return jdbcTemplate.queryForObject(
                "SELECT count_labels_with_bands_above(?)",
                new Object[]{threshold},
                Integer.class
        );
    }

    // Получение списка описаний по префиксу
    public List<Map<String, Object>> findDescriptionsByPrefix(String prefix) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM find_descriptions_by_prefix(?)",
                prefix
        );
    }

    // Добавление сингла к группе
    public void addSingleToBand(int bandId, int singlesToAdd) {
        validateUserPermission(bandId); // Проверка прав
        jdbcTemplate.execute(
                "SELECT add_single_to_band(?, ?)",
                (PreparedStatement ps) -> {
                    ps.setInt(1, bandId);
                    ps.setInt(2, singlesToAdd);
                    ps.execute();
                    return null; // Результат функции игнорируется
                }
        );
    }

    // Добавление участника в группу
    public void addParticipantToBand(int bandId, int participantsToAdd) {
        validateUserPermission(bandId); // Проверка прав
        jdbcTemplate.execute(
                "SELECT add_participant_to_band(?, ?)",
                (PreparedStatement ps) -> {
                    ps.setInt(1, bandId);
                    ps.setInt(2, participantsToAdd);
                    ps.execute();
                    return null;
                }
        );
    }
}
