package com.finanzapp.app_financiera.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "planned_payments")
@RequiredArgsConstructor
@NoArgsConstructor
public class PlannedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NonNull private int userId;

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
    
    private LocalDate paymentDate;

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
