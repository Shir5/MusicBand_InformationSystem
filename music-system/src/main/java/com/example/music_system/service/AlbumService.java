package com.example.music_system.service;

import com.example.music_system.model.Album;
import com.example.music_system.model.Role;
import com.example.music_system.model.User;
import com.example.music_system.repository.AlbumRepository;
import com.example.music_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository; // Для поиска текущего пользователя
    private final SimpMessagingTemplate messagingTemplate;

    public AlbumService(AlbumRepository albumRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Получение всех альбомов
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    // Получение альбома по ID
    public Optional<Album> getAlbumById(Integer id) {
        return albumRepository.findById(id);
    }

    // Создание нового альбома
    public Album createAlbum(Album album, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        album.setCreatedBy(user);
        Album savedAlbum = albumRepository.save(album);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/albums", savedAlbum);

        return savedAlbum;
    }


    // Обновление альбома

    public Album updateAlbum(Integer id, Album album, String username) {
        Album existingAlbum = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!existingAlbum.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to update this album.");
        }

        existingAlbum.setName(album.getName());
        existingAlbum.setTracks(album.getTracks());
        Album updatedAlbum = albumRepository.save(existingAlbum);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/albums", updatedAlbum);

        return updatedAlbum;
    }
    public void deleteAlbum(Integer id, String username) {
        Album existingAlbum = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!existingAlbum.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to delete this album.");
        }

        albumRepository.delete(existingAlbum);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/albums",
                Map.of("action", "delete", "id", id));
    }

}

