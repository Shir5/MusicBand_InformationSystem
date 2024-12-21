package com.example.music_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "music_band")
public class MusicBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название группы не может быть пустым.")
    @Column(nullable = false)
    private String name;

    @Embedded
    @NotNull(message = "Координаты обязательны.")
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
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
    @JsonIgnore // Прерываем цикл сериализации
    @ManyToOne
    @JoinColumn(name = "label_id", nullable = true)
    private Label label;
    @JsonIgnore // Прерываем цикл сериализации

    @ManyToOne
    @JoinColumn(name = "best_album_id", nullable = true) // Связь с лучшим альбомом
    private Album bestAlbum;
    @JsonIgnore // Прерываем цикл сериализации

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
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

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Album getBestAlbum() {
        return bestAlbum;
    }

    public void setBestAlbum(Album bestAlbum) {
        this.bestAlbum = bestAlbum;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
