package com.example.autopark.autopark.controller;

import com.example.autopark.autopark.entity.*;
import com.example.autopark.autopark.repository.UserRepository;
import com.example.autopark.autopark.service.JwtService;
import com.example.autopark.autopark.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Контроллер для управления пользователями.
 * Предоставляет REST API для регистрации, аутентификации, получения, создания и удаления пользователей.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Конструктор класса UserController.
     *
     * @param userService  Сервис для работы с пользователями
     * @param userRepository Репозиторий для доступа к данным пользователей
     * @param jwtService   Сервис для работы с JWT-токенами
     */
    public UserController(UserService userService, UserRepository userRepository, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param user Объект пользователя, переданный в теле запроса
     * @return ResponseEntity с созданным пользователем
     */
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Регистрирует нового пользователя на основе данных, переданных в формате Map.
     *
     * @param userData Map с данными пользователя (username и password)
     * @return ResponseEntity с сообщением об успешной регистрации или ошибкой, если пользователь уже существует
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");

        if (userService.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // Хешируем пароль
        user.setRole(Role.CUSTOMER);  // По умолчанию обычный пользователь

        userRepository.save(user);

        return ResponseEntity.ok("Пользователь зарегистрирован.");
    }

    /**
     * Получает список всех пользователей.
     *
     * @return Список всех пользователей в формате ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Получает пользователя по его ID.
     *
     * @param id ID пользователя
     * @return ResponseEntity с пользователем, если он найден, или статус 404 (Not Found), если пользователь отсутствует
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Создает нового пользователя.
     *
     * @param user Объект пользователя, переданный в теле запроса
     * @return ResponseEntity с сохраненным пользователем
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    /**
     * Удаляет пользователя по его ID.
     *
     * @param id ID пользователя
     * @return ResponseEntity со статусом 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Аутентифицирует пользователя и возвращает JWT-токен.
     *
     * @param loginData Map с данными для входа (username и password)
     * @return ResponseEntity с JWT-токеном и информацией о пользователе при успешной аутентификации,
     *         или статус 401 (Unauthorized) при неверных учетных данных
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty() || !userService.checkPassword(password, userOptional.get())) {
            return ResponseEntity.status(401).body("Неверные учетные данные.");
        }

        User user = userOptional.get();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(Map.of("token", token, "id", user.getId(), "userType", user.getRole()));
    }

    /**
     * Ищет пользователя по имени пользователя (username).
     *
     * @param username Имя пользователя для поиска
     * @return ResponseEntity с найденным пользователем (если существует)
     */
    @GetMapping("/search")
    public ResponseEntity<Optional<User>> searchUsers(@RequestParam String username) {
        Optional<User> users = userRepository.findByUsername(username);
        return ResponseEntity.ok(users);
    }
}