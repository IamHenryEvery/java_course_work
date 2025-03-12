package com.example.autopark.autopark.repository;

import com.example.autopark.autopark.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Репозиторий для работы с бронированиями.
 * Расширяет JpaRepository для выполнения CRUD-операций и предоставляет дополнительные методы
 * для поиска бронирований по пользователю или автомобилю.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования, связанные с указанным пользователем.
     *
     * @param userId ID пользователя
     * @return Список бронирований, принадлежащих пользователю
     */
    List<Booking> findByUserId(Long userId);

    /**
     * Находит все бронирования, связанные с указанным автомобилем.
     *
     * @param carId ID автомобиля
     * @return Список бронирований, связанных с автомобилем
     */
    List<Booking> findByCarId(Long carId);
}