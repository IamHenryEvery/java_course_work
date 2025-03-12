package com.example.autopark.autopark.repository;

import com.example.autopark.autopark.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторий для работы с автомобилями.
 * Расширяет JpaRepository для выполнения CRUD-операций и предоставляет дополнительные методы
 * для поиска доступных автомобилей.
 */
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Находит все автомобили, которые доступны для бронирования.
     * Доступные автомобили — это те, у которых поле "available" установлено в true.
     *
     * @return Список доступных автомобилей
     */
    List<Car> findByAvailableTrue();
}