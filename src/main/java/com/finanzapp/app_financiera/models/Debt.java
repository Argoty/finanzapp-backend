package com.finanzapp.app_financiera.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Debt {
    private final String id = UUID.randomUUID().toString();
    @NonNull private String userId;
    @NonNull private String title;
    @NonNull private double accumulatedAmount ;
    @NonNull private double totalDebt;
    @NonNull private LocalDate date;

}
