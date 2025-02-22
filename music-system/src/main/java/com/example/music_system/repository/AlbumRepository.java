package com.example.music_system.repository;

import com.example.music_system.model.Album;
import com.example.music_system.model.MusicBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Optional<Album> findByName(String name);

    boolean existsByName(String name);

}
