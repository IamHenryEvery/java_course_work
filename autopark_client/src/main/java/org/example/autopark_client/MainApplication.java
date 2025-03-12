package org.example.autopark_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Главный класс приложения для запуска клиентской части системы Autopark.
 * Использует JavaFX для создания графического интерфейса пользователя.
 */
public class MainApplication extends Application {

    /**
     * Точка входа в JavaFX приложение.
     * Загружает FXML-файл главного окна, настраивает сцену и отображает окно.
     *
     * @param primaryStage Основная сцена (Stage) приложения
     * @throws Exception Возможные исключения при загрузке FXML или настройке сцены
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML для главного окна
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/autopark_client/mainWindow.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Autopark");
        primaryStage.setScene(scene);

        // Устанавливаем начальный размер окна 800x600
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        // Отображаем окно
        primaryStage.show();
    }

    /**
     * Точка входа в приложение.
     * Запускает JavaFX приложение.
     *
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}