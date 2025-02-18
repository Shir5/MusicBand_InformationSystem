package com.example.music_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "import_history")
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String status; // Например, "SUCCESS" или "FAILURE"

    @Column(nullable = false)
    private int addedObjects;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // === Добавляем новые поля для хранения информации о загруженном файле ===
    @Column(name = "file_name", nullable = true)  // nullable = true, если поле может быть пустым
    private String fileName;

    @Column(name = "file_object_name", nullable = true)
    private String fileObjectName;

    // Геттеры и сеттеры для новых полей
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileObjectName() {
        return fileObjectName;
    }

    public void setFileObjectName(String fileObjectName) {
        this.fileObjectName = fileObjectName;
    }

    // Геттеры и сеттеры для остальных полей
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAddedObjects() {
        return addedObjects;
    }

    public void setAddedObjects(int addedObjects) {
        this.addedObjects = addedObjects;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
