package com.example.music_system.util;

import com.example.music_system.dto.MusicBandImportDto;
import com.example.music_system.repository.AlbumRepository;
import com.example.music_system.repository.LabelRepository;
import com.example.music_system.repository.MusicBandRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;

@Component
public class ValidationAndDuplicateChecker {

    private final MusicBandRepository musicBandRepository;
    private final LabelRepository labelRepository;
    private final AlbumRepository albumRepository;

    public ValidationAndDuplicateChecker(MusicBandRepository musicBandRepository, LabelRepository labelRepository, AlbumRepository albumRepository) {
        this.musicBandRepository = musicBandRepository;
        this.labelRepository = labelRepository;
        this.albumRepository = albumRepository;
    }

    public void validateAndCheckForDuplicates(MusicBandImportDto importDto) {
        validateConstraints(importDto);

        checkForDuplicates(importDto);
    }

    private void validateConstraints(MusicBandImportDto importDto) {
        if (importDto.getName() == null || importDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название группы не может быть пустым.");
        }

        if (importDto.getCoordinates() == null || importDto.getCoordinates().getX() == null || importDto.getCoordinates().getY() == null) {
            throw new IllegalArgumentException("Координаты обязательны.");
        }

        if (musicBandRepository.existsByCoordinates(importDto.getCoordinates().getX(), importDto.getCoordinates().getY())) {
            throw new IllegalArgumentException("Группа с указанными координатами уже существует.");
        }

        if (importDto.getNumberOfParticipants() < 1 || importDto.getNumberOfParticipants() > 100) {
            throw new IllegalArgumentException("Количество участников должно быть от 1 до 100.");
        }


        if (importDto.getEstablishmentDate().isAfter(LocalDate.now().atStartOfDay(ZoneId.systemDefault()))) {
            throw new IllegalArgumentException("Дата основания группы не может быть в будущем.");
        }

        if (importDto.getCoordinates().getY() < -495) {
            throw new IllegalArgumentException("Координата Y должна быть больше -495.");
        }

        if (importDto.getGenre() == null) {
            throw new IllegalArgumentException("Жанр обязателен.");
        }

        if (importDto.getNumberOfParticipants() <= 0) {
            throw new IllegalArgumentException("Количество участников должно быть больше 0.");
        }

        if (importDto.getAlbumsCount() == null || importDto.getAlbumsCount() <= 0) {
            throw new IllegalArgumentException("Количество альбомов должно быть больше 0.");
        }

        if (importDto.getDescription() == null || importDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым.");
        }

        if (importDto.getLabel() == null || importDto.getLabel().getName() == null || importDto.getLabel().getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название лейбла не может быть пустым.");
        }

        if (importDto.getLabel().getBands() == null || importDto.getLabel().getBands() < 0) {
            throw new IllegalArgumentException("Количество групп в лейбле не может быть отрицательным.");
        }

        if (importDto.getBestAlbum() == null || importDto.getBestAlbum().getName() == null || importDto.getBestAlbum().getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название альбома не может быть пустым.");
        }

        if (importDto.getBestAlbum().getTracks() == null || importDto.getBestAlbum().getTracks() <= 0) {
            throw new IllegalArgumentException("Количество треков в альбоме должно быть больше 0.");
        }
    }

    private void checkForDuplicates(MusicBandImportDto importDto) {
        if (musicBandRepository.existsByName(importDto.getName())) {
            throw new IllegalArgumentException("Группа с названием '" + importDto.getName() + "' уже существует.");
        }

        if (labelRepository.existsByName(importDto.getLabel().getName())) {
            throw new IllegalArgumentException("Лейбл с названием '" + importDto.getLabel().getName() + "' уже существует.");
        }

        if (albumRepository.existsByName(importDto.getBestAlbum().getName())) {
            throw new IllegalArgumentException("Альбом с названием '" + importDto.getBestAlbum().getName() + "' уже существует.");
        }
    }
}
