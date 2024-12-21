package com.example.music_system.repository;

import com.example.music_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Аннотация @Repository помечает интерфейс как компонент уровня доступа к данным
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Поиск пользователя по имени пользователя
    Optional<User> findByUsername(String username);
}
