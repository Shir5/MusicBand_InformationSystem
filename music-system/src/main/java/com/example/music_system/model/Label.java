package com.example.music_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "label")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название лейбла не может быть пустым.")
    @Column(nullable = false)
    private String name;

    @PositiveOrZero(message = "Количество групп не может быть отрицательным.")
    @Column(nullable = false)
    private Long bands; // Количество групп, связанных с лейблом

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_band_id", nullable = true)
    @JsonBackReference // Обратная ссылка
    private MusicBand musicBand; // Связь с MusicBand

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    // Конструкторы
    public Label() {
        // Пустой конструктор для JPA
    }

    public Label(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.bands = 0L; // Значение по умолчанию
    }

    public Label(Integer id, String name, Long bands) {
        this.id = id;
        this.name = name;
        this.bands = bands;
    }


    // Геттеры и сеттеры
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
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

    public MusicBand getMusicBand() {
        return musicBand;
    }

    public void setMusicBand(MusicBand musicBand) {
        this.musicBand = musicBand;
    }


}
