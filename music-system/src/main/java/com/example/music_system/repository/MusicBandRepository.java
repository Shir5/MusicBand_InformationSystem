package com.example.music_system.repository;

import com.example.music_system.model.MusicBand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusicBandRepository extends JpaRepository<MusicBand, Integer> {
    Page<MusicBand> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<MusicBand> findByName(String name);

}
