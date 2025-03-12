package com.example.autopark.autopark.service;

import com.example.autopark.autopark.entity.Car;
import com.example.autopark.autopark.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с автомобилями.
 * Предоставляет методы для получения, добавления, удаления и обновления автомобилей.
 */
@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    /**
     * Получает список всех доступных автомобилей.
     *
     * @return Список доступных автомобилей
     */
    public List<Car> getAvailableCars() {
        return carRepository.findByAvailableTrue();  // Получение всех доступных автомобилей
    }

    /**
     * Добавляет новый автомобиль в базу данных.
     *
     * @param car Объект автомобиля для добавления
     */
    public void addCar(Car car) {
        carRepository.save(car);  // Добавление нового автомобиля
    }

    /**
     * Удаляет автомобиль по его ID.
     *
     * @param id ID автомобиля для удаления
     */
    public void deleteCar(Long id) {
        carRepository.deleteById(id);  // Удаление автомобиля по ID
    }

    /**
     * Обновляет данные существующего автомобиля.
     *
     * @param car Объект автомобиля с обновленными данными
     */
    public void updateCar(Car car) {
        carRepository.save(car);  // Обновление данных автомобиля
    }
}