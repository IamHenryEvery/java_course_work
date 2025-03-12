package com.example.autopark.autopark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для запуска системы аренды автомобилей.
 * Использует Spring Boot для автоматической настройки и запуска приложения.
 */
@SpringBootApplication
public class CarRentalSystemApplication {

	/**
	 * Точка входа в приложение.
	 * Запускает Spring Boot приложение.
	 *
	 * @param args Аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(CarRentalSystemApplication.class, args);
	}
}