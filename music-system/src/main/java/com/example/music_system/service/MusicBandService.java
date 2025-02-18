package com.example.music_system.service;

import com.example.music_system.dto.MusicBandDto;
import com.example.music_system.dto.MusicBandImportDto;
import com.example.music_system.model.*;
import com.example.music_system.repository.*;
import com.example.music_system.util.MusicBandDtoValidator;
import com.example.music_system.util.ValidationAndDuplicateChecker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.music_system.service.DatabaseInitializer.logger;

@Service
public class MusicBandService {

    private final MusicBandRepository musicBandRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final AlbumRepository albumRepository;
    private final MusicBandMapper musicBandMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ImportHistoryRepository importHistoryRepository;
    private final ValidationAndDuplicateChecker validationAndDuplicateChecker;
    private final MusicBandDtoValidator musicBandDtoValidator;
    private final MinioFileService minioFileService;


    @PersistenceContext
    private EntityManager entityManager;
    public MusicBandService(
            MusicBandRepository musicBandRepository,
            UserRepository userRepository,
            LabelRepository labelRepository,
            AlbumRepository albumRepository,
            MusicBandMapper musicBandMapper,
            SimpMessagingTemplate messagingTemplate,
            ImportHistoryRepository importHistoryRepository,
            ValidationAndDuplicateChecker validationAndDuplicateChecker, MusicBandDtoValidator musicBandDtoValidator, MinioFileService minioFileService) {
        this.musicBandRepository = musicBandRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.albumRepository = albumRepository;
        this.musicBandMapper = musicBandMapper;
        this.messagingTemplate = messagingTemplate;
        this.importHistoryRepository = importHistoryRepository;
        this.validationAndDuplicateChecker = validationAndDuplicateChecker;
        this.musicBandDtoValidator = musicBandDtoValidator;
        this.minioFileService = minioFileService;
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
    @Transactional(isolation = Isolation.SERIALIZABLE)
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
        musicBandDtoValidator.validateAndCheckForDuplicates(dto);

        MusicBand createdBand = musicBandRepository.save(band);
        MusicBandDto createdDto = musicBandMapper.toDto(createdBand);

        // Публикуем сообщение о создании
        messagingTemplate.convertAndSend("/topic/bands", createdDto);

        return createdDto;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
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
        musicBandDtoValidator.validateAndCheckForDuplicates(dto);

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

        // Удаляем связанный лейбл, если он существует
        Label label = existingBand.getLabel();
        if (label != null) {
            existingBand.setLabel(null);
            musicBandRepository.save(existingBand);
            labelRepository.delete(label);
        }

        // Удаляем саму группу
        musicBandRepository.delete(existingBand);

        // Публикуем сообщение об удалении
        messagingTemplate.convertAndSend("/topic/bands", Map.of("action", "delete", "id", id));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importBandsWithEntities(List<MusicBandImportDto> importDtos, String username, MultipartFile file) throws Exception {
        ImportHistory history = new ImportHistory();
        history.setUsername(username);
        history.setTimestamp(LocalDateTime.now());

        int successfullyAdded = 0;
        String objectName = null;

        try {
            // 1. Подготовка: Загрузка файла в MinIO
            if (file != null && !file.isEmpty()) {
                objectName = minioFileService.uploadFile(file);
                history.setFileName(file.getOriginalFilename());
                history.setFileObjectName(objectName);
            }
            // 2. Подготовка: Сохранение данных в БД
            for (MusicBandImportDto importDto : importDtos) {
                // Валидация и проверка дубликатов
                validationAndDuplicateChecker.validateAndCheckForDuplicates(importDto);

                // Создание и сохранение Label
                Label label = new Label();
                label.setName(importDto.getLabel().getName());
                label.setBands(importDto.getLabel().getBands());
                label.setCreatedBy(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
                Label savedLabel = labelRepository.save(label);

                // Создание и сохранение Album
                Album album = new Album();
                album.setName(importDto.getBestAlbum().getName());
                album.setTracks(importDto.getBestAlbum().getTracks());
                album.setCreatedBy(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
                Album savedAlbum = albumRepository.save(album);

                // Создание и сохранение MusicBand
                MusicBand band = new MusicBand();
                band.setName(importDto.getName());
                band.setCoordinates(new Coordinates(importDto.getCoordinates().getX(), importDto.getCoordinates().getY()));
                band.setGenre(importDto.getGenre());
                band.setNumberOfParticipants(importDto.getNumberOfParticipants());
                band.setSinglesCount(importDto.getSinglesCount());
                band.setDescription(importDto.getDescription());
                band.setAlbumsCount(importDto.getAlbumsCount());
                band.setEstablishmentDate(importDto.getEstablishmentDate());
                band.setCreatedBy(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found")));
                band.setLabel(savedLabel);
                band.setBestAlbum(savedAlbum);

                musicBandRepository.save(band);
                successfullyAdded++;

                // Уведомление о прогрессе
                messagingTemplate.convertAndSend("/topic/bands", Map.of(
                        "action", "progress",
                        "message", "Imported band: " + importDto.getName(),
                        "successfullyAdded", successfullyAdded,
                        "remainingItems", importDtos.size() - successfullyAdded
                ));
            }

            // 3. Коммит: Фиксация успешной операции
            history.setStatus("SUCCESS");
            history.setAddedObjects(successfullyAdded);

            messagingTemplate.convertAndSend("/topic/bands", Map.of(
                    "action", "complete",
                    "message", "Import completed successfully",
                    "successfullyAdded", successfullyAdded
            ));
        } catch (Exception e) {
            // 4. Откат: Установка статуса неудачи
            history.setStatus("FAILURE");
            history.setAddedObjects(successfullyAdded);

            messagingTemplate.convertAndSend("/topic/bands", Map.of(
                    "action", "failure",
                    "message", "Import failed",
                    "successfullyAdded", successfullyAdded,
                    "error", e.getMessage()
            ));

            // 5. Откат: Удаление загруженного файла из MinIO, если он был загружен
            if (objectName != null) {
                try {
                    minioFileService.removeFile(objectName);
                } catch (Exception ex) {
                    // Логируем, но не прерываем основной откат
                    logger.error("Failed to remove file from MinIO: {}", ex.getMessage());
                }
            }

            throw e;
        } finally {
            // 6. Сохранение истории импорта
            importHistoryRepository.save(history);
        }
    }


}