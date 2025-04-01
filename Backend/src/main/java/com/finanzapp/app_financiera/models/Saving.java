package com.finanzapp.app_financiera.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Saving {
    private final String id = UUID.randomUUID().toString();
    @NonNull private String title;
    @NonNull private  double accumulatedAmount ;
    @NonNull private double goaldAmount;
    @NonNull private String category;
}
