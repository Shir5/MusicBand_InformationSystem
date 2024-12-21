package com.example.music_system.dto;

import com.example.music_system.dto.CoordinatesDto;
import com.example.music_system.model.MusicGenre;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class MusicBandDto {

    private Integer id;

    @NotBlank(message = "Название не может быть пустым.")
    private String name;

    @NotNull(message = "Координаты обязательны.")
    private CoordinatesDto coordinates; // Используем DTO для координат

    @NotNull(message = "Жанр обязателен.")
    private MusicGenre genre;

    @Positive(message = "Количество участников должно быть больше 0.")
    private long numberOfParticipants;

    @PositiveOrZero(message = "Количество синглов должно быть больше или равно 0.")
    private Integer singlesCount;

    @NotBlank(message = "Описание не может быть пустым.")
    private String description;

    @Positive(message = "Количество альбомов должно быть больше 0.")
    private Integer albumsCount;

    @NotNull(message = "Дата основания обязательна.")
    private ZonedDateTime establishmentDate;

    @NotNull
    private LocalDate creationDate;

    private Integer labelId; // ID связанного лейбла

    private Integer bestAlbumId; // ID лучшего альбома, который может быть связан с группой


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinatesDto getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDto coordinates) {
        this.coordinates = coordinates;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(long numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Integer getSinglesCount() {
        return singlesCount;
    }

    public void setSinglesCount(Integer singlesCount) {
        this.singlesCount = singlesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAlbumsCount() {
        return albumsCount;
    }

    public void setAlbumsCount(Integer albumsCount) {
        this.albumsCount = albumsCount;
    }

    public ZonedDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(ZonedDateTime establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    public Integer getBestAlbumId() {
        return bestAlbumId;
    }

    public void setBestAlbumId(Integer bestAlbumId) {
        this.bestAlbumId = bestAlbumId;
    }
}
