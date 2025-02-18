package com.example.music_system.repository;

import com.example.music_system.model.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Integer> {
    // Обычный пользователь видит только свои операции
    List<ImportHistory> findByUsername(String username);


    // Администратор видит все операции
    @Query("SELECT h FROM ImportHistory h")
    List<ImportHistory> findAllOperations();
}
