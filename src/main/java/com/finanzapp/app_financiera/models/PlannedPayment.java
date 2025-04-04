package com.finanzapp.app_financiera.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
//import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class PlannedPayment {

    private String id = UUID.randomUUID().toString();
    @NonNull
    private String userId;

    @NonNull
    private String type;
    @NonNull
    private String category;
    @NonNull
    private String name;

//    @NonNull
//    private String frequency;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate; // Fecha de vencimiento o pago

    @NonNull
    private int amount;

//    public PlannedPayment(@NonNull String type,
//                          @NonNull String category,
//                          @NonNull String name,
//                          String frequency,
//                          @NonNull LocalDate dueDate,
//                          @NonNull int amount) {
//        this.type = type;
//        this.category = category;
//        this.name = name;
//        this.frequency = frequency;
//        this.dueDate = dueDate;
//        this.amount = amount;
//    }
}
