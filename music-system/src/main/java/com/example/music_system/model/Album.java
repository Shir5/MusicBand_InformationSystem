package com.example.music_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название альбома не может быть пустым.")
    @Column(nullable = false)
    private String name; // Поле не может быть null, строка не может быть пустой

    @Positive(message = "Количество треков должно быть больше 0.")
    @Column(nullable = false)
    private long tracks; // Значение поля больше 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_band_id", nullable = true)
    @JsonBackReference // Обратная ссылка для предотвращения цикла
    private MusicBand musicBand; // Связь с MusicBand
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    // Конструкторы
    public Album() {
        // Пустой конструктор для JPA
    }

    public Album(Integer id, String name, long tracks) {
        this.id = id;
        this.name = name;
        this.tracks = tracks;
    }

    // Геттеры и Сеттеры

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

    public long getTracks() {
        return tracks;
    }

    public void setTracks(long tracks) {
        this.tracks = tracks;
    }

    public MusicBand getMusicBand() {
        return musicBand;
    }

    public void setMusicBand(MusicBand musicBand) {
        this.musicBand = musicBand;
    }
}
