package com.example.autopark.autopark.controller;

import com.example.autopark.autopark.entity.Car;
import com.example.autopark.autopark.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления машинами.
 * Предоставляет REST API для работы с машинами, включая получение, создание, удаление и поиск доступных машин.
 */
@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    /**
     * Получает список всех машин.
     *
     * @return Список всех машин в формате ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    /**
     * Получает машину по её ID.
     *
     * @param id ID машины
     * @return ResponseEntity с машиной, если она найдена, или статус 404 (Not Found), если машина отсутствует
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Добавляет новую машину в систему.
     *
     * @param car Объект машины, переданный в теле запроса
     * @return ResponseEntity с сохраненной машиной
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity.ok(carRepository.save(car));
    }

    /**
     * Удаляет машину по её ID.
     *
     * @param id ID машины
     * @return ResponseEntity с сообщением об успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
        return ResponseEntity.ok("Машина успешно удалена");
    }

    /**
     * Получает список всех доступных машин.
     * Доступные машины — это те, у которых поле "available" установлено в true.
     *
     * @return Список доступных машин в формате ResponseEntity
     */
    @GetMapping("/available")
    public ResponseEntity<List<Car>> getAvailableCars() {
        List<Car> cars = carRepository.findByAvailableTrue(); // Получаем доступные машины из базы данных
        return ResponseEntity.ok(cars);
    }
}