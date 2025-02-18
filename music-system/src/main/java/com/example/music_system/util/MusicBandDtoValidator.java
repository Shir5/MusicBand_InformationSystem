package com.example.music_system.util;

import com.example.music_system.dto.MusicBandDto;
import com.example.music_system.repository.AlbumRepository;
import com.example.music_system.repository.LabelRepository;
import com.example.music_system.repository.MusicBandRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Component
public class MusicBandDtoValidator {

    private final MusicBandRepository musicBandRepository;
    private final LabelRepository labelRepository;
    private final AlbumRepository albumRepository;

    public MusicBandDtoValidator(MusicBandRepository musicBandRepository, LabelRepository labelRepository, AlbumRepository albumRepository) {
        this.musicBandRepository = musicBandRepository;
        this.labelRepository = labelRepository;
        this.albumRepository = albumRepository;
    }

    public void validateAndCheckForDuplicates(MusicBandDto dto) {
        validateConstraints(dto);
        checkForDuplicates(dto);
    }

    private void validateConstraints(MusicBandDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название группы не может быть пустым.");
        }

        if (dto.getCoordinates() == null || dto.getCoordinates().getX() == null || dto.getCoordinates().getY() == null) {
            throw new IllegalArgumentException("Координаты обязательны.");
        }

        if (dto.getCoordinates().getY() < -495) {
            throw new IllegalArgumentException("Координата Y должна быть больше -495.");
        }

        if (dto.getGenre() == null) {
            throw new IllegalArgumentException("Жанр обязателен.");
        }

        if (dto.getNumberOfParticipants() <= 0 || dto.getNumberOfParticipants() > 100) {
            throw new IllegalArgumentException("Количество участников должно быть от 1 до 100.");
        }

        if (dto.getAlbumsCount() == null || dto.getAlbumsCount() <= 0) {
            throw new IllegalArgumentException("Количество альбомов должно быть больше 0.");
        }

        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым.");
        }

        if (dto.getEstablishmentDate() == null || dto.getEstablishmentDate().isAfter(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Дата основания группы не может быть в будущем.");
        }

        if (dto.getLabelId() != null && !labelRepository.existsById(dto.getLabelId())) {
            throw new IllegalArgumentException("Лейбл с ID: " + dto.getLabelId() + " не найден.");
        }

        if (dto.getBestAlbumId() != null && !albumRepository.existsById(dto.getBestAlbumId())) {
            throw new IllegalArgumentException("Альбом с ID: " + dto.getBestAlbumId() + " не найден.");
        }
    }

    private void checkForDuplicates(MusicBandDto dto) {
        if (musicBandRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Группа с названием '" + dto.getName() + "' уже существует.");
        }

        if (dto.getLabelId() != null) {
            String labelName = labelRepository.findById(dto.getLabelId())
                    .orElseThrow(() -> new IllegalArgumentException("Лейбл с ID: " + dto.getLabelId() + " не найден."))
                    .getName();
            if (labelRepository.existsByName(labelName)) {
                throw new IllegalArgumentException("Лейбл с названием '" + labelName + "' уже существует.");
            }
        }

        if (dto.getBestAlbumId() != null) {
            String albumName = albumRepository.findById(dto.getBestAlbumId())
                    .orElseThrow(() -> new IllegalArgumentException("Альбом с ID: " + dto.getBestAlbumId() + " не найден."))
                    .getName();
            if (albumRepository.existsByName(albumName)) {
                throw new IllegalArgumentException("Альбом с названием '" + albumName + "' уже существует.");
            }
        }
    }
}
