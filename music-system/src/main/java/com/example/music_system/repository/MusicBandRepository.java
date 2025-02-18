package com.example.music_system.repository;

import com.example.music_system.model.MusicBand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusicBandRepository extends JpaRepository<MusicBand, Integer> {
    Page<MusicBand> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<MusicBand> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM MusicBand b WHERE b.coordinates.x = :x AND b.coordinates.y = :y")
    boolean existsByCoordinates(@Param("x") Integer x, @Param("y") Float y);

}
