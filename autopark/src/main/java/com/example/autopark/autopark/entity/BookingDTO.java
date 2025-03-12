package com.example.autopark.autopark.entity;
import java.time.LocalDate;
import lombok.*;

@Getter @Setter
public class BookingDTO {

    private Long Id;
    private Long userId;
    private Long carId; // ID машины
    private LocalDate startDate;
    private LocalDate endDate;
}
