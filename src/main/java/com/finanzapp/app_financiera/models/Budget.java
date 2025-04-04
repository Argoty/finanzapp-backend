package com.finanzapp.app_financiera.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Budget {

    private String id = UUID.randomUUID().toString();
    @NonNull
    private String userId;

    @NonNull
    private String category;
    @NonNull
    private String name;
    @NonNull
    private String period;
    @NonNull
    private int limitAmount;

}
