package com.finanzapp.app_financiera.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "debts")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private int userId;

    @NonNull
    private String title;

    @NonNull
    private double accumulatedAmount;

    @NonNull
    private double totalDebt;

    @NonNull
    private LocalDate date;
}
