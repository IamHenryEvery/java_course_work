package org.example.autopark_client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.autopark_client.service.ServerService;
import org.example.autopark_client.dto.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для административной панели.
 * Управляет отображением таблиц пользователей, автомобилей и бронирований,
 * а также предоставляет функционал для создания, удаления и поиска данных.
 */
public class AdminController {

    private final String SERVER_URL = "http://localhost:8080"; // URL сервера
    private final ServerService serverService = new ServerService(); // Сервис для работы с сервером
    private final HttpClient httpClient = HttpClient.newHttpClient(); // HTTP клиент для отправки запросов
    private final String token = serverService.getToken(); // Токен для аутентификации
    private MainController mainController; // Главный контроллер

    /**
     * Устанавливает главный контроллер для взаимодействия между панелями.
     *
     * @param mainController Главный контроллер
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private StackPane tablePane;

    /**
     * Переключает интерфейс на главную панель и закрывает текущую.
     */
    @FXML
    private void switchToMain() {
        if (mainController != null) {
            mainController.switchToMain(); // Переключаемся на карту через MainController
            Stage stage = (Stage) tablePane.getScene().getWindow();
            stage.close(); // Закрываем текущую панель
        } else {
            System.err.println("Ошибка: MainController не установлен.");
        }
    }

    @FXML
    private GridPane adminGridPane;
    @FXML
    private VBox createUserPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private TextField searchUserField;
    @FXML
    private TextField searchIdField;
    @FXML
    private TableView<UserDTO> usersTable;
    @FXML
    private TableColumn<UserDTO, Long> userIdColumn;
    @FXML
    private TableColumn<UserDTO, String> usernameColumn;
    @FXML
    private TableColumn<UserDTO, String> passwordColumn;
    @FXML
    private TableColumn<UserDTO, String> UserRoleColumn;
    @FXML
    private Button addUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private TableView<CarDTO> carsTable;
    @FXML
    private TableColumn<CarDTO, Long> carIdColumn;
    @FXML
    private TableColumn<CarDTO, String> carBrandColumn;
    @FXML
    private TableColumn<CarDTO, String> carModelColumn;
    @FXML
    private TableColumn<CarDTO, Integer> carYearColumn;
    @FXML
    private TableColumn<CarDTO, Double> carPricePerDayColumn;
    @FXML
    private TableColumn<CarDTO, Boolean> carAvailableColumn;
    @FXML
    private TableView<BookingDTO> bookingsTable;
    @FXML
    private TableColumn<BookingDTO, Long> bookingIdColumn;
    @FXML
    private TableColumn<BookingDTO, Long> bookingUserIdColumn;
    @FXML
    private TableColumn<BookingDTO, Long> bookingCarIdColumn;
    @FXML
    private TableColumn<BookingDTO, LocalDate> bookingStartDateColumn;
    @FXML
    private TableColumn<BookingDTO, LocalDate> bookingEndDateColumn;
    private ObservableList<UserDTO> users;
    private ObservableList<CarDTO> cars;

    /**
     * Инициализирует контроллер после загрузки FXML.
     * Настраивает отображение таблиц и обработчики событий.
     */
    public void initialize() {
        userIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        passwordColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPassword()));
        UserRoleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        bookingIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        bookingUserIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getUserId()));
        bookingCarIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCarId()));
        bookingStartDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStartDate()));
        bookingEndDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEndDate()));
        carIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        carBrandColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getBrand()));
        carModelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getModel()));
        carYearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getYear()));
        carPricePerDayColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPricePerDay()));
        carAvailableColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().isAvailable()));

        // Обработчики для двойного щелчка на строках таблиц
        usersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                UserDTO selectedUser = usersTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    handleDeleteUser(selectedUser); // Обработка удаления пользователя
                }
            }
        });
    }

    /**
     * Показывает форму для создания нового пользователя.
     */
    @FXML
    private void showCreateUserForm() {
        showOnly(createUserPane);
    }

    /**
     * Создаёт нового пользователя с заданными параметрами и добавляет его в систему.
     * Если создание пользователя прошло успешно, обновляется таблица пользователей.
     */
    @FXML
    private void createUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String userType = roleChoiceBox.getValue();
        if (username.isEmpty() || password.isEmpty() || userType == null) {
            mainController.showError("Заполните все поля!");
            return;
        }
        UserDTO newUser = new UserDTO(username, password, userType);
        boolean success = serverService.createUser(newUser);
        if (success) {
            showUsersTable();
            mainController.showSuccess("Пользователь успешно создан!");
        } else {
            mainController.showError("Ошибка при создании пользователя.");
        }
    }

    /**
     * Обрабатывает удаление пользователя. Показывает подтверждение удаления и, если пользователь подтверждает,
     * удаляет его из системы.
     *
     * @param user Пользователь, которого нужно удалить.
     */
    private void handleDeleteUser(UserDTO user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление пользователя");
        alert.setContentText("Вы уверены, что хотите удалить пользователя " + user.getUsername() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = serverService.deleteUser(user.getId());
            if (success) {
                usersTable.getItems().remove(user); // Удаляем из таблицы
                mainController.showSuccess("Пользователь удалён!");
            } else {
                mainController.showError("Ошибка при удалении пользователя.");
            }
        }
    }

    /**
     * Показывает только указанный элемент интерфейса, скрывая остальные.
     *
     * @param node Элемент интерфейса для отображения
     */
    private void showOnly(Node node) {
        usersTable.setVisible(false);
        usersTable.setManaged(false);
        createUserPane.setVisible(false);
        createUserPane.setManaged(false);
        carsTable.setVisible(false);
        carsTable.setManaged(false);
        bookingsTable.setVisible(false);
        bookingsTable.setManaged(false);
        node.setVisible(true);
        node.setManaged(true);
    }

    /**
     * Получение списка пользователей с сервера.
     *
     * @return Список пользователей
     */
    public List<UserDTO> getUsers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(SERVER_URL + "/api/users"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<List<UserDTO>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Показывает таблицу пользователей, обновляя её содержимое с сервера.
     */
    @FXML
    private void showUsersTable() {
        showOnly(usersTable);
        createUserPane.setVisible(false);
        createUserPane.setManaged(false);
        List<UserDTO> users = getUsers();
        usersTable.getItems().setAll(users);
    }

    /**
     * Ищет пользователя по имени (username). Если пользователь найден, отображает его в таблице.
     */
    @FXML
    private void searchUserByUsername() {
        String username = searchUserField.getText().trim();
        if (username.isEmpty()) {
            mainController.showError("Введите username для поиска.");
            return;
        }
        Optional<UserDTO> user = serverService.findUserByUsername(username);
        if (user.isPresent()) {
            // Отображаем найденного пользователя в таблице
            usersTable.setItems(FXCollections.observableArrayList(user.get()));
        } else {
            mainController.showError("Пользователь с именем \"" + username + "\" не найден.");
        }
    }

    /**
     * Ищет бронирования по ID пользователя. Если бронирования найдены, отображает их в таблице.
     */
    @FXML
    private void searchBookingsByUserId() {
        String userIdText = searchIdField.getText().trim();
        if (userIdText.isEmpty()) {
            mainController.showError("Введите ID пользователя для поиска.");
            return;
        }
        try {
            Long userId = Long.parseLong(userIdText);
            List<BookingDTO> bookings = serverService.findBookingsByUserId(userId);
            if (bookings.isEmpty()) {
                mainController.showError("Бронирования для пользователя с ID " + userId + " не найдены.");
            } else {
                bookingsTable.setItems(FXCollections.observableArrayList(bookings));
            }
        } catch (NumberFormatException e) {
            mainController.showError("ID пользователя должен быть числом.");
        }
    }

    /**
     * Получение списка бронирований с сервера.
     *
     * @return Список бронирований
     */
    public List<BookingDTO> getBookings() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(SERVER_URL + "/api/bookings"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(response.body(), new TypeReference<List<BookingDTO>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Показывает таблицу бронирований, обновляя её содержимое с сервера.
     */
    @FXML
    private void showBookingsTable() {
        showOnly(bookingsTable);
        List<BookingDTO> bookings = getBookings();
        bookingsTable.getItems().setAll(bookings);
    }

    /**
     * Получение списка автомобилей с сервера.
     *
     * @return Список автомобилей
     */
    public List<CarDTO> getCars() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(SERVER_URL + "/api/cars"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<List<CarDTO>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Показывает таблицу автомобилей, обновляя её содержимое с сервера.
     */
    @FXML
    private void showCarsTable() {
        showOnly(carsTable);
        List<CarDTO> cars = getCars();
        carsTable.getItems().setAll(cars);
    }

    /**
     * Показывает диалоговое окно с сообщением.
     *
     * @param title       Заголовок окна
     * @param content     Текст сообщения
     * @param alertType   Тип диалогового окна (например, INFORMATION, ERROR)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Показывает информацию об авторе приложения.
     */
    @FXML
    private void showAboutAuthor() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Об авторе");
        alert.setHeaderText("Курсовая работа по JavaFX");
        alert.setContentText("""
            Автор: Гришин Никита
            Группа: ПИ22-1
            Почта: 222703@edu.fa.ru
            Преподаватель: Свирина А.Г.
            Версия приложения: 1.0
            Все права защищены.
            """);
        alert.showAndWait();
    }
}