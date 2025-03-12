package com.example.autopark.autopark.repository;

import com.example.autopark.autopark.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Расширяет JpaRepository для выполнения CRUD-операций и предоставляет дополнительные методы
 * для поиска пользователей по имени пользователя (username).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его имени пользователя (username).
     *
     * @param username Имя пользователя для поиска
     * @return Optional, содержащий пользователя, если он найден, или пустой Optional, если пользователь отсутствует
     */
    Optional<User> findByUsername(String username);
}