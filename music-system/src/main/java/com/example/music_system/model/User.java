package com.example.music_system.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Уникальный идентификатор

    @Column(nullable = false, unique = true)
    private String username; // Имя пользователя, уникальное, не может быть null

    @Column(nullable = false)
    private String passwordHash; // Хэш пароля, не может быть null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Роль пользователя: ADMIN или USER

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt; // Дата регистрации, заполняется автоматически

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MusicBand> createdBands; // Список созданных групп

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<MusicBand> getCreatedBands() {
        return createdBands;
    }

    public void setCreatedBands(List<MusicBand> createdBands) {
        this.createdBands = createdBands;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}
