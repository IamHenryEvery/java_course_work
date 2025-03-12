package com.example.autopark.autopark.service;

import com.example.autopark.autopark.entity.*;
import com.example.autopark.autopark.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с бронированиями.
 * Предоставляет методы для получения, создания и отмены бронирований.
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Получает все бронирования для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return Список бронирований, связанных с пользователем
     */
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);  // Получение всех бронирований для пользователя
    }

    /**
     * Получает все бронирования для указанного автомобиля.
     *
     * @param carId ID автомобиля
     * @return Список бронирований, связанных с автомобилем
     */
    public List<Booking> getBookingsByCar(Long carId) {
        return bookingRepository.findByCarId(carId);  // Получение всех бронирований для автомобиля
    }

    /**
     * Создает новое бронирование на основе данных DTO.
     *
     * @param bookingDTO DTO с данными для создания бронирования
     * @throws RuntimeException Если машина или пользователь не найдены
     */
    public void createBooking(BookingDTO bookingDTO) {
        // Поиск машины по carId
        Car car = carRepository.findById(bookingDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Машина не найдена"));

        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Создание нового бронирования
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car); // Устанавливаем найденный объект Car
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());

        bookingRepository.save(booking);
    }

    /**
     * Отменяет бронирование по его ID.
     *
     * @param bookingId ID бронирования
     */
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);  // Отмена бронирования
    }

    /**
     * Получает все бронирования из базы данных.
     *
     * @return Список всех бронирований
     */
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
}