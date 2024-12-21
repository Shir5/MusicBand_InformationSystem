package com.example.music_system.repository;

import com.example.music_system.model.AdminRequest;
import com.example.music_system.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Integer> {
    List<AdminRequest> findByStatus(RequestStatus status);
}
