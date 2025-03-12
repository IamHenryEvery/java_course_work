package com.example.autopark.autopark.service;

import com.example.autopark.autopark.entity.Role;
import com.example.autopark.autopark.entity.User;
import com.example.autopark.autopark.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для регистрации, поиска, проверки паролей и управления пользователями.
 */
@Service
public class UserService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Конструктор класса UserService.
     *
     * @param userRepo Репозиторий для работы с данными пользователей
     */
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Инициализация
    }

    /**
     * Регистрирует нового пользователя.
     * Хеширует пароль и устанавливает роль по умолчанию (CUSTOMER), если роль не указана.
     *
     * @param user Объект пользователя для регистрации
     * @return Сохраненный объект пользователя
     */
    public User registerUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);  // Устанавливаем тип по умолчанию
        }
        return userRepo.save(user);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return Список всех пользователей
     */
    public List<User> findAll() {
        return userRepo.findAll();
    }

    /**
     * Находит пользователя по имени пользователя (username).
     *
     * @param username Имя пользователя для поиска
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional, если пользователь отсутствует
     */
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * Находит пользователя по его ID.
     *
     * @param id ID пользователя
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional, если пользователь отсутствует
     */
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    /**
     * Проверяет, соответствует ли переданный пароль хешу пароля пользователя.
     *
     * @param rawPassword Нехешированный пароль
     * @param user        Объект пользователя
     * @return true, если пароль совпадает, иначе false
     */
    public boolean checkPassword(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * Проверяет, существует ли пользователь с указанным именем пользователя.
     *
     * @param username Имя пользователя для проверки
     * @return true, если пользователь существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return userRepo.findByUsername(username).isPresent();
    }
}