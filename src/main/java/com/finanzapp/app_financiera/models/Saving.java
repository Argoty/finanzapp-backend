package com.finanzapp.app_financiera.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Table(name = "savings")
@NoArgsConstructor
@AllArgsConstructor
public class Saving {
    @Id
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String userId;

    @NonNull
    private String title;

    @NonNull
    private  double accumulatedAmount ;

    @NonNull
    private double goalAmount;

    @NonNull
    private String category;
}
