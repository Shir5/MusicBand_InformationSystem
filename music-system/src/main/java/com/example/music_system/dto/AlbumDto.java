package com.example.music_system.dto;

import jakarta.validation.constraints.*;

public class AlbumDto {
    private Integer id; // Уникальный идентификатор альбома

    @NotBlank(message = "Название альбома не может быть пустым.")
    private String name;

    @NotNull(message = "Количество треков обязательно.")
    @Positive(message = "Количество треков должно быть больше 0.")
    private Long tracks;

    // Геттеры и сеттеры
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

    public Long getTracks() {
        return tracks;
    }

    public void setTracks(Long tracks) {
        this.tracks = tracks;
    }
}
