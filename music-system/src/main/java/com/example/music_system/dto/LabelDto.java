package com.example.music_system.dto;

import jakarta.validation.constraints.*;

public class LabelDto {
    private Integer id; // Уникальный идентификатор лейбла

    private String name; // Поле для имени лейбла

    @NotNull(message = "Количество групп обязательно.")
    private Long bands;

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

    public Long getBands() {
        return bands;
    }

    public void setBands(Long bands) {
        this.bands = bands;
    }
}
