package com.example.music_system.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Coordinates {

    @NotNull(message = "Координата X обязательна.")
    private Integer x;

    @NotNull(message = "Координата Y обязательна.")
    @Min(value = -495, message = "Координата Y должна быть больше -495.")
    private Float y;

    // Конструктор по умолчанию
    public Coordinates() {
    }

    // Конструктор для x и y
    public Coordinates(Integer x, Float y) {
        this.x = x;
        this.y = y;
    }

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

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
