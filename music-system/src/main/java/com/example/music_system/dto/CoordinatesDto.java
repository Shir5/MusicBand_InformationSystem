package com.example.music_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CoordinatesDto {

    @NotNull(message = "Координата X обязательна.")
    private Integer x;

    @NotNull(message = "Координата Y обязательна.")
    @Min(value = -495, message = "Координата Y должна быть больше -495.")
    private Float y;

    // Геттеры и сеттеры
    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
