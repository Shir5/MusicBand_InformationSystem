package com.example.music_system.service;

import com.example.music_system.model.Label;
import com.example.music_system.model.Role;
import com.example.music_system.model.User;
import com.example.music_system.repository.LabelRepository;
import com.example.music_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LabelService(LabelRepository labelRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.labelRepository = labelRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    public Optional<Label> getLabelById(Integer id) {
        return labelRepository.findById(id);
    }

    public Label createLabel(Label label, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        label.setCreatedBy(user);
        Label savedLabel = labelRepository.save(label);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/labels", savedLabel);

        return savedLabel;
    }

    public Label updateLabel(Integer id, Label label, String username) {
        Label existingLabel = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!existingLabel.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to update this label.");
        }

        existingLabel.setName(label.getName());
        existingLabel.setBands(label.getBands());
        Label updatedLabel = labelRepository.save(existingLabel);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/labels", updatedLabel);

        return updatedLabel;
    }

    public void deleteLabel(Integer id, String username) {
        Label existingLabel = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!existingLabel.getCreatedBy().getUsername().equals(username) && !user.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to delete this label.");
        }

        labelRepository.delete(existingLabel);

        // Отправка обновлений через WebSocket
        messagingTemplate.convertAndSend("/topic/labels", Map.of("action", "delete", "id", id));
    }

}
