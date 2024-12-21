package com.example.music_system.service;

import com.example.music_system.dto.CoordinatesDto;
import com.example.music_system.dto.MusicBandDto;
import com.example.music_system.model.Album;
import com.example.music_system.model.Coordinates;
import com.example.music_system.model.Label;
import com.example.music_system.model.MusicBand;
import com.example.music_system.repository.AlbumRepository;
import com.example.music_system.repository.LabelRepository;
import org.springframework.stereotype.Component;

@Component
public class MusicBandMapper {

    private final LabelRepository labelRepository;
    private final AlbumRepository albumRepository;

    public MusicBandMapper(LabelRepository labelRepository, AlbumRepository albumRepository) {
        this.labelRepository = labelRepository;
        this.albumRepository = albumRepository;
    }

    public MusicBandDto toDto(MusicBand band) {
        MusicBandDto dto = new MusicBandDto();
        dto.setId(band.getId());
        dto.setName(band.getName());

        // Преобразование координат
        if (band.getCoordinates() != null) {
            CoordinatesDto coordinatesDto = new CoordinatesDto();
            coordinatesDto.setX(band.getCoordinates().getX());
            coordinatesDto.setY(band.getCoordinates().getY());
            dto.setCoordinates(coordinatesDto);
        }

        dto.setGenre(band.getGenre());
        dto.setNumberOfParticipants(band.getNumberOfParticipants());
        dto.setSinglesCount(band.getSinglesCount());
        dto.setDescription(band.getDescription());
        dto.setAlbumsCount(band.getAlbumsCount());
        dto.setEstablishmentDate(band.getEstablishmentDate());
        dto.setCreationDate(band.getCreationDate());

        // Устанавливаем labelId
        if (band.getLabel() != null) {
            dto.setLabelId(band.getLabel().getId());
        }

        // Устанавливаем bestAlbumId
        if (band.getBestAlbum() != null) {
            dto.setBestAlbumId(band.getBestAlbum().getId());
        }

        return dto;
    }

    public MusicBand toEntity(MusicBandDto dto) {
        MusicBand band = new MusicBand();
        band.setId(dto.getId());
        band.setName(dto.getName());

        // Преобразование координат
        if (dto.getCoordinates() != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setX(dto.getCoordinates().getX());
            coordinates.setY(dto.getCoordinates().getY());
            band.setCoordinates(coordinates);
        }

        band.setGenre(dto.getGenre());
        band.setNumberOfParticipants(dto.getNumberOfParticipants());
        band.setSinglesCount(dto.getSinglesCount());
        band.setDescription(dto.getDescription());
        band.setAlbumsCount(dto.getAlbumsCount());
        band.setEstablishmentDate(dto.getEstablishmentDate());

        // Преобразуем labelId в Label
        if (dto.getLabelId() != null) {
            Label label = labelRepository.findById(dto.getLabelId())
                    .orElseThrow(() -> new RuntimeException("Label not found with ID: " + dto.getLabelId()));
            band.setLabel(label);
        }

        // Преобразуем bestAlbumId в Album
        if (dto.getBestAlbumId() != null) {
            Album bestAlbum = albumRepository.findById(dto.getBestAlbumId())
                    .orElseThrow(() -> new RuntimeException("Album not found with ID: " + dto.getBestAlbumId()));
            band.setBestAlbum(bestAlbum);
        }

        return band;
    }
}
