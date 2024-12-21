package com.example.music_system.service;

import com.example.music_system.dto.MusicBandDto;
import com.example.music_system.model.*;
import com.example.music_system.repository.AlbumRepository;
import com.example.music_system.repository.LabelRepository;
import com.example.music_system.repository.MusicBandRepository;
import com.example.music_system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MusicBandService {

    private final MusicBandRepository musicBandRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final AlbumRepository albumRepository;
    private final MusicBandMapper musicBandMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @PersistenceContext
    private EntityManager entityManager;
    public MusicBandService(
            MusicBandRepository musicBandRepository,
            UserRepository userRepository,
            LabelRepository labelRepository,
            AlbumRepository albumRepository,
            MusicBandMapper musicBandMapper,
            SimpMessagingTemplate messagingTemplate) {
        this.musicBandRepository = musicBandRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.albumRepository = albumRepository;
        this.musicBandMapper = musicBandMapper;
        this.messagingTemplate = messagingTemplate;
    }


    public Page<MusicBandDto> getAllBands(String filter, Pageable pageable) {
        Page<MusicBand> bands = filter == null || filter.isBlank()
                ? musicBandRepository.findAll(pageable)
                : musicBandRepository.findByNameContainingIgnoreCase(filter, pageable);

        return bands.map(musicBandMapper::toDto);
    }


    public Optional<MusicBandDto> getBandById(Integer id) {
        return musicBandRepository.findById(id).map(musicBandMapper::toDto);
    }

    public MusicBandDto createBand(MusicBandDto dto, String username) {
        MusicBand band = musicBandMapper.toEntity(dto);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        band.setCreatedBy(user);
        band.setCreationDate(LocalDate.now());

        if (dto.getLabelId() != null) {
            Label label = labelRepository.findById(dto.getLabelId())
                    .orElseThrow(() -> new RuntimeException("Label not found with ID: " + dto.getLabelId()));
            band.setLabel(label);
        }

        if (dto.getBestAlbumId() != null) {
            Album bestAlbum = albumRepository.findById(dto.getBestAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with ID: " + dto.getBestAlbumId()));
            band.setBestAlbum(bestAlbum);
        }

        MusicBand createdBand = musicBandRepository.save(band);
        MusicBandDto createdDto = musicBandMapper.toDto(createdBand);

        // Публикуем сообщение о создании
        messagingTemplate.convertAndSend("/topic/bands", createdDto);

        return createdDto;
    }

    public MusicBandDto updateBand(Integer id, MusicBandDto dto, String username) {
        MusicBand existingBand = musicBandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Band not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Проверяем, является ли пользователь создателем или администратором
        if (!existingBand.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to update this band.");
        }

        MusicBand updatedBand = musicBandMapper.toEntity(dto);
        updatedBand.setId(existingBand.getId());
        updatedBand.setCreatedBy(existingBand.getCreatedBy());
        updatedBand.setCreationDate(existingBand.getCreationDate());

        if (dto.getLabelId() != null) {
            Label label = labelRepository.findById(dto.getLabelId())
                    .orElseThrow(() -> new RuntimeException("Label not found with ID: " + dto.getLabelId()));
            updatedBand.setLabel(label);
        }

        if (dto.getBestAlbumId() != null) {
            Album bestAlbum = albumRepository.findById(dto.getBestAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with ID: " + dto.getBestAlbumId()));
            updatedBand.setBestAlbum(bestAlbum);
        } else {
            updatedBand.setBestAlbum(null);
        }

        MusicBand savedBand = musicBandRepository.save(updatedBand);
        MusicBandDto updatedDto = musicBandMapper.toDto(savedBand);

        // Публикуем сообщение об обновлении
        messagingTemplate.convertAndSend("/topic/bands", updatedDto);

        return updatedDto;
    }

    public void deleteBand(Integer id, String username) {
        MusicBand existingBand = musicBandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Band not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Проверяем, является ли пользователь создателем или администратором
        if (!existingBand.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to delete this band.");
        }

        // Удаляем связь между группой и альбомом
        Album bestAlbum = existingBand.getBestAlbum();
        if (bestAlbum != null) {
            existingBand.setBestAlbum(null);
            musicBandRepository.save(existingBand);
            albumRepository.delete(bestAlbum);
        }

        musicBandRepository.delete(existingBand);

        // Публикуем сообщение об удалении
        messagingTemplate.convertAndSend("/topic/bands", Map.of("action", "delete", "id", id));
    }
}
