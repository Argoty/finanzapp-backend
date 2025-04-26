package com.finanzapp.app_financiera.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Table(name = "savings")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Saving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private int userId;

    @NonNull
    private String title;

    @NonNull
    private  double accumulatedAmount ;

    @NonNull
    private double goalAmount;

    @NonNull
    private String category;
}
