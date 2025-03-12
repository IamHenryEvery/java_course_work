package com.example.autopark.autopark.controller;

import com.example.autopark.autopark.entity.Booking;
import com.example.autopark.autopark.entity.BookingDTO;
import com.example.autopark.autopark.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления бронированиями.
 * Предоставляет REST API для работы с бронированиями, включая создание, отмену и получение данных о бронированиях.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Получает все бронирования для указанного пользователя.
     *
     * @param userId ID пользователя
     * @return Список бронирований пользователя в формате ResponseEntity
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    /**
     * Получает все бронирования для указанной машины.
     *
     * @param carId ID машины
     * @return Список бронирований машины в формате ResponseEntity
     */
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Booking>> getBookingsByCar(@PathVariable Long carId) {
        return ResponseEntity.ok(bookingService.getBookingsByCar(carId));
    }

    /**
     * Создает новое бронирование на основе данных, переданных в теле запроса.
     *
     * @param bookingDTO DTO с данными для создания бронирования
     * @return ResponseEntity с сообщением об успешном создании или ошибке
     */
    @PostMapping
    public ResponseEntity<String> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            bookingService.createBooking(bookingDTO);
            return ResponseEntity.ok("Бронирование успешно создано");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Отменяет бронирование по его ID.
     *
     * @param bookingId ID бронирования
     * @return ResponseEntity с сообщением об успешной отмене
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Бронирование успешно отменено");
    }

    /**
     * Получает все бронирования в системе.
     * Преобразует список бронирований в DTO для передачи клиенту.
     *
     * @return Список DTO всех бронирований в формате ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(bookingDTOs);
    }

    /**
     * Преобразует объект бронирования (Booking) в DTO (BookingDTO).
     *
     * @param booking Объект бронирования для преобразования
     * @return DTO с данными бронирования
     */
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setUserId(booking.getUser().getId());
        dto.setCarId(booking.getCar().getId());

        return dto;
    }
}