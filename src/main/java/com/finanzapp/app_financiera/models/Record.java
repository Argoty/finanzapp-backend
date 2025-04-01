package com.finanzapp.app_financiera.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // Constructor vacío para Jackson
@RequiredArgsConstructor
public class Record {

    private String id = UUID.randomUUID().toString();
    @NonNull
    private String type;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    @NonNull
    private String category;
    private String description;
    private int amount;

    public Record(@NonNull String type, @NonNull LocalDateTime date, @NonNull String category, String description, int amount) {
        this.type = type;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }
}
