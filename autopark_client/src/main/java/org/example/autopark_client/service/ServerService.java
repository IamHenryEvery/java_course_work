package org.example.autopark_client.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;

import org.example.autopark_client.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import javafx.scene.control.Alert;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Сервис для взаимодействия с сервером.
 * Предоставляет методы для выполнения HTTP-запросов, таких как логин, регистрация,
 * создание и удаление пользователей, работа с бронированиями и автомобилями.
 */
public class ServerService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String token;
    private Long id;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Конструктор класса ServerService.
     * Регистрирует модуль JavaTimeModule для поддержки работы с типами даты и времени.
     */
    public ServerService() {
        // Регистрируем модуль JavaTimeModule
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Возвращает экземпляр HttpClient для выполнения HTTP-запросов.
     *
     * @return Экземпляр HttpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Устанавливает токен авторизации.
     *
     * @param token Токен авторизации
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Возвращает текущий токен авторизации.
     *
     * @return Текущий токен авторизации
     */
    public String getToken() {
        return token;
    }

    /**
     * Возвращает ID текущего пользователя.
     *
     * @return ID текущего пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает ID текущего пользователя.
     *
     * @param id ID пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     * @return Роль пользователя (например, "ADMIN" или "CUSTOMER") при успешном входе, иначе null
     */
    public String login(String username, String password) {
        try {
            String jsonBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/login"))  // Путь к логину
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Используем ObjectMapper для парсинга JSON ответа
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.body());
                this.token = root.get("token").asText();  // Сохраняем токен
                this.id = root.get("id").asLong();
                System.out.println("Токен получен: " + getToken());
                return root.get("userType").asText();
            } else {
                showError("Ошибка логина");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Регистрирует нового пользователя на сервере.
     *
     * @param username Имя пользователя
     * @param password Пароль пользователя
     * @return true, если регистрация успешна, иначе false
     */
    public boolean register(String username, String password) {
        try {
            String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;  // Успех при статусе 200
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Создает нового пользователя на сервере.
     *
     * @param user DTO с данными пользователя
     * @return true, если создание успешно, иначе false
     */
    public boolean createUser(UserDTO user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users"))
                    .header("Authorization", "Bearer " + getToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет пользователя по его ID.
     *
     * @param userId ID пользователя
     * @return true, если удаление успешно, иначе false
     */
    public boolean deleteUser(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/" + userId))
                    .header("Authorization", "Bearer " + getToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username Имя пользователя
     * @return Optional, содержащий UserDTO, если пользователь найден, иначе пустой Optional
     */
    public Optional<UserDTO> findUserByUsername(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/search?username=" + username))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Десериализуем JSON в UserDTO
                UserDTO user = objectMapper.readValue(response.body(), UserDTO.class);
                return Optional.ofNullable(user);
            } else {
                System.err.println("HTTP Error: " + response.statusCode() + " - " + response.body());
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Получает список бронирований для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return Список бронирований (BookingDTO), если запрос успешен, иначе пустой список
     */
    public List<BookingDTO> findBookingsByUserId(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/bookings/user/" + userId))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<BookingDTO>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Получает список доступных автомобилей.
     *
     * @return Список доступных автомобилей (CarDTO), если запрос успешен, иначе пустой список
     */
    public List<CarDTO> getAvailableCars() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/cars/available"))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<CarDTO>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Создает новое бронирование.
     *
     * @param booking DTO с данными бронирования
     * @return true, если создание успешно, иначе false
     */
    public boolean createBooking(BookingDTO booking) {
        try {
            String json = objectMapper.writeValueAsString(booking);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/bookings"))
                    .header("Authorization", "Bearer " + getToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Получает список бронирований для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return Список бронирований (BookingDTO), если запрос успешен, иначе пустой список
     */
    public List<BookingDTO> getBookingsByUser(Long userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/bookings/user/" + userId))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<BookingDTO>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Получает ID пользователя по его имени.
     *
     * @param username Имя пользователя
     * @return ID пользователя, если запрос успешен, иначе null
     */
    public Long getUserId(String username) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/users/search?username=" + username))
                    .header("Authorization", "Bearer " + getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                UserDTO user = objectMapper.readValue(response.body(), UserDTO.class);
                return user.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Отображает диалоговое окно с сообщением об ошибке.
     *
     * @param message Сообщение об ошибке
     */
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}