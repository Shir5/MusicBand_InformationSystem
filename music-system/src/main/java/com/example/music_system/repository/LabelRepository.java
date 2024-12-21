package com.example.music_system.repository;

import com.example.music_system.model.Label;
import com.example.music_system.model.MusicBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Integer> {
    Optional<Label> findByName(String name);

}
