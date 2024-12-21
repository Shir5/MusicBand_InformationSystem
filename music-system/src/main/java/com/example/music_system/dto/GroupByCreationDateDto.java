package com.example.music_system.dto;

import java.time.LocalDate;

public class GroupByCreationDateDto {
    private LocalDate creationDate;
    private Integer count;

    public GroupByCreationDateDto(LocalDate creationDate, Integer count) {
        this.creationDate = creationDate;
        this.count = count;
    }

    // Геттеры и сеттеры
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
