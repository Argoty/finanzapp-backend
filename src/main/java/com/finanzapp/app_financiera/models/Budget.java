package com.finanzapp.app_financiera.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "budgets")
@RequiredArgsConstructor
@NoArgsConstructor  
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NonNull private int userId;

    @NonNull
    private String category;
    @NonNull
    private String name;
    @NonNull
    private String period;
    @NonNull
    private int limitAmount;

}
