package com.example.music_system.dto;

import com.example.music_system.model.MusicGenre;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;

public class MusicBandImportDto {

    @NotBlank(message = "Название группы не может быть пустым.")
    private String name;

    @NotNull(message = "Координаты обязательны.")
    private CoordinatesDto coordinates;

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

    @Valid
    @NotNull(message = "Данные лейбла обязательны.")
    private LabelDto label;

    @Valid
    @NotNull(message = "Данные альбома обязательны.")
    private AlbumDto bestAlbum;

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

    public LabelDto getLabel() {
        return label;
    }

    public void setLabel(LabelDto label) {
        this.label = label;
    }

    public AlbumDto getBestAlbum() {
        return bestAlbum;
    }

    public void setBestAlbum(AlbumDto bestAlbum) {
        this.bestAlbum = bestAlbum;
    }


}
