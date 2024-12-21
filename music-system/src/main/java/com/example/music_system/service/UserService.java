package com.example.music_system.service;

import com.example.music_system.dto.RegisterRequest;
import com.example.music_system.model.AdminRequest;
import com.example.music_system.model.RequestStatus;
import com.example.music_system.model.Role;
import com.example.music_system.model.User;
import com.example.music_system.repository.AdminRequestRepository;
import com.example.music_system.repository.UserRepository;
import com.example.music_system.security.PasswordUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminRequestRepository adminRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public UserService(UserRepository userRepository, AdminRequestRepository adminRequestRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.adminRequestRepository = adminRequestRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Регистрация нового пользователя
    public void registerUser(RegisterRequest request, int minPasswordLength) {
        validateRegisterRequest(request, minPasswordLength);
        boolean hasAdmins = userRepository.findAll().stream().anyMatch(User::isAdmin);
        User newUser = createNewUser(request, hasAdmins);

        if (hasAdmins && request.getIsAdminRequest()) {
            System.out.println("Creating admin request for user: " + newUser.getUsername());
            handleAdminRequest(newUser);
        } else {
            userRepository.save(newUser);
        }
    }

    // Проверка корректности запроса регистрации
    private void validateRegisterRequest(RegisterRequest request, int minPasswordLength) {
        if (request.getPassword().length() < minPasswordLength) {
            throw new IllegalArgumentException("Password must be at least " + minPasswordLength + " characters long.");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already exists.");
        }
    }

    // Создание нового пользователя
    private User createNewUser(RegisterRequest request, boolean hasAdmins) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
        newUser.setRole(hasAdmins ? Role.USER : Role.ADMIN);
        return newUser;
    }

    // Обработка заявки на роль администратора
    private void handleAdminRequest(User newUser) {
        newUser.setRole(Role.USER);
        userRepository.save(newUser);
        createAdminRequest(newUser);
        throw new IllegalStateException("Your request to become an admin is pending approval.");
    }

    // Создание заявки на роль администратора
    public void createAdminRequest(User user) {
        System.out.println("Creating admin request for user: " + user.getUsername());
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User does not exist.");
        }

        if (user.getRole() != Role.USER) {
            throw new IllegalArgumentException("Only users can request admin role.");
        }

        if (adminRequestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .anyMatch(request -> request.getUser().equals(user))) {
            throw new IllegalArgumentException("You already have a pending request.");
        }

        AdminRequest request = new AdminRequest();
        request.setUser(user);
        request.setStatus(RequestStatus.PENDING);
        adminRequestRepository.save(request);
        System.out.println("Admin request saved for user: " + user.getUsername());
    }


    // Получение всех заявок со статусом PENDING
    public List<AdminRequest> getPendingAdminRequests() {
        return adminRequestRepository.findByStatus(RequestStatus.PENDING);
    }

    // Обработка заявки (одобрение или отклонение)
    public void processAdminRequest(Integer requestId, boolean approve) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (approve) {
            User user = request.getUser();
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            request.setStatus(RequestStatus.APPROVED);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }

        adminRequestRepository.save(request);

            }

    // Аутентификация пользователя
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getPasswordHash().equals(PasswordUtil.hashPassword(password))) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    // Найти пользователя по имени
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Получить пользователя по ID
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found."));
    }

    // Удалить пользователя по ID
    public void deleteUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found."));
        userRepository.delete(user);
    }

    // Обновить роль пользователя
    public User updateUserRole(Integer id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found."));
        user.setRole(role);
        return userRepository.save(user);
    }


}
