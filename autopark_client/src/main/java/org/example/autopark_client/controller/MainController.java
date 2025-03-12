package org.example.autopark_client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.autopark_client.dto.*;
import org.example.autopark_client.service.ServerService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Главный контроллер приложения.
 * Управляет логикой взаимодействия пользователя с интерфейсом,
 * включая вход, регистрацию, бронирование автомобилей и просмотр данных.
 */
public class MainController {

    private final ServerService serverService = new ServerService();
    private String userType;
    private Long userId; // ID текущего пользователя

    @FXML
    private VBox bookingFormPane; // Панель для формы бронирования
    @FXML
    private ComboBox<CarDTO> carChoiceBox; // Выбор машины
    @FXML
    private DatePicker startDatePicker, endDatePicker;
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
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    // Логин
    @FXML
    private VBox loginPane;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginErrorLabel;
    // Регистрация
    @FXML
    private VBox registerPane;
    @FXML
    private TextField registerUsernameField, registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private Label registerErrorLabel;
    // Главный экран
    @FXML
    private Pane mapPane, mainPane;
    @FXML
    private Button toAdminPanelButton;

    /**
     * Инициализация контроллера.
     * Скрывает карту при запуске, отображая форму логина.
     */
    @FXML
    public void initialize() {
        mapPane.setVisible(false);
        bookingFormPane.setVisible(false);
        bookingFormPane.setManaged(false);
        bookingIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getId()));
        bookingUserIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getUserId()));
        bookingCarIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCarId()));
        bookingStartDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStartDate()));
        bookingEndDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEndDate()));
    }

    /**
     * Переключает на админ-панель.
     * Загружает FXML-файл админ-панели и передает управление AdminController.
     */
    @FXML
    private void switchToAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark_client/AdminPanel.fxml"));
            Parent adminRoot = loader.load();
            AdminController adminController = loader.getController();
            adminController.setMainController(this);
            Stage adminStage = new Stage();
            adminStage.setTitle("Админ-панель");
            adminStage.setScene(new Scene(adminRoot, 800, 600));
            adminStage.show();
            switchToMain();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает вход пользователя в систему.
     * Проверяет данные и переключает интерфейс в зависимости от роли пользователя.
     */
    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Пожалуйста, заполните все поля.");
            return;
        }
        userType = serverService.login(username, password);
        if (userType != null) {
            loginErrorLabel.setText("");
            // Получаем ID пользователя после успешного входа
            userId = serverService.getUserId(username);
            if (userId == null) {
                showError("Не удалось получить ID пользователя.");
                return;
            }
            if ("ADMIN".equals(userType)) {
                switchToAdminPanel();
            } else {
                switchToMain();
            }
        } else {
            loginErrorLabel.setText("Ошибка логина. Проверьте данные.");
        }
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     * Проверяет данные и регистрирует пользователя на сервере.
     */
    @FXML
    private void handleRegister() {
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerErrorLabel.setText("Пожалуйста, заполните все поля.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            registerErrorLabel.setText("Пароли не совпадают.");
            return;
        }
        boolean success = serverService.register(username, password);
        if (success) {
            showSuccess("Регистрация успешна! Теперь войдите.");
            switchToLogin();
        } else {
            registerErrorLabel.setText("Ошибка регистрации. Попробуйте другой логин.");
        }
    }

    /**
     * Обрабатывает выход из системы.
     * Сбрасывает текущий токен и переключает на форму входа.
     */
    @FXML
    private void handleLogout() {
        serverService.setToken(null); // Удаляем токен
        serverService.setId(null);
        toAdminPanelButton.setVisible(false);
        switchToLogin();
    }

    /**
     * Переключает видимость панели на экран входа.
     * Скрывает экраны регистрации и главного меню.
     */
    @FXML
    private void switchToLogin() {
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        mapPane.setVisible(false);
        mapPane.setManaged(false);
        loginUsernameField.clear();
        loginPasswordField.clear();
        loginErrorLabel.setText("");
    }

    /**
     * Переключает видимость панели на экран регистрации.
     * Скрывает экран входа.
     */
    @FXML
    private void switchToRegister() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
    }

    /**
     * Переключает на основной экран пользователя.
     * Скрывает экраны входа и регистрации, сбрасывает карту и отображает
     * кнопку перехода в админ-панель для администраторов.
     */
    @FXML
    public void switchToMain() {
        resetMap();
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        mapPane.setVisible(true);
        mapPane.setManaged(true);
        if ("ADMIN".equals(userType)) {
            toAdminPanelButton.setVisible(true); // Показываем кнопку для админа
        }
    }

    /**
     * Отображает форму для создания бронирования.
     * Загружает доступные автомобили и настраивает интерфейс выбора.
     */
    @FXML
    private void showBookingForm() {
        if (userId == null) {
            showError("Пользователь не авторизован.");
            return;
        }
        // Загружаем доступные машины
        List<CarDTO> availableCars = serverService.getAvailableCars();
        if (availableCars.isEmpty()) {
            showError("Нет доступных машин для бронирования.");
            return;
        }
        carChoiceBox.setItems(FXCollections.observableArrayList(availableCars));
        // Настройка отображения машин в ChoiceBox
        carChoiceBox.setCellFactory(param -> new ListCell<CarDTO>() {
            @Override
            protected void updateItem(CarDTO car, boolean empty) {
                super.updateItem(car, empty);
                if (car == null || empty) {
                    setText(null);
                } else {
                    setText(car.getBrand() + " " + car.getModel());
                }
            }
        });
        carChoiceBox.setButtonCell(new ListCell<CarDTO>() {
            @Override
            protected void updateItem(CarDTO car, boolean empty) {
                super.updateItem(car, empty);
                if (car == null || empty) {
                    setText(null);
                } else {
                    setText(car.getBrand() + " " + car.getModel());
                }
            }
        });
        bookingFormPane.setVisible(true);
        bookingFormPane.setManaged(true);
    }

    /**
     * Создает новое бронирование на основе выбранных данных.
     * Проверяет корректность введенных дат и отправляет запрос на сервер.
     */
    @FXML
    private void createBooking() {
        CarDTO selectedCar = carChoiceBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if (selectedCar == null || startDate == null || endDate == null) {
            showError("Заполните все поля.");
            return;
        }
        if (startDate.isAfter(endDate)) {
            showError("Дата начала должна быть раньше даты окончания.");
            return;
        }
        BookingDTO booking = new BookingDTO();
        booking.setUserId(userId);
        booking.setCarId(selectedCar.getId());
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        boolean success = serverService.createBooking(booking);
        if (success) {
            showSuccess("Бронирование успешно создано!");
            bookingFormPane.setVisible(false);
            bookingFormPane.setManaged(false);
        } else {
            showError("Не удалось создать бронирование.");
        }
    }

    /**
     * Отображает таблицу с бронированиями текущего пользователя.
     */
    @FXML
    private void viewMyBookings() {
        if (userId == null) {
            showError("Пользователь не авторизован.");
            return;
        }
        List<BookingDTO> bookings = serverService.getBookingsByUser(userId);
        ObservableList<BookingDTO> observableBookings = FXCollections.observableArrayList(bookings);
        bookingsTable.setItems(observableBookings);
    }

    /**
     * Устанавливает ID текущего пользователя после входа.
     *
     * @param userId ID пользователя
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Сбрасывает форму бронирования, очищая все поля.
     */
    @FXML
    private void resetBookingForm() {
        carChoiceBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    /**
     * Отображает информационное окно с данными об авторе приложения.
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

    /**
     * Показывает диалоговое окно с сообщением об успехе.
     *
     * @param message Текст сообщения
     */
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешно!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке.
     *
     * @param message Текст сообщения
     */
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Сбрасывает фон карты на изображение по умолчанию.
     */
    @FXML
    private void resetMap() {
        changeBackground("/images/salon.jpg");
    }

    /**
     * Изменяет фон карты на указанное изображение.
     *
     * @param imagePath Путь к изображению
     */
    public void changeBackground(String imagePath) {
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, false
                )
        );

        mapPane.setBackground(new Background(backgroundImage));
    }
}