package com.finanzapp.app_financiera.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debts")
public class Debt {

    @Id
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String userId;

    @NonNull
    private String title;

    @NonNull
    private double accumulatedAmount;

    @NonNull
    private double totalDebt;

    @NonNull
    private LocalDate date;
}
