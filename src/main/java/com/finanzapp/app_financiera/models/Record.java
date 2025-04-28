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
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "records")
@NoArgsConstructor  
@RequiredArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NonNull private int userId;
    
    @NonNull
    private String type;
    
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;
    
    @NonNull
    private String category;
    
    private String description;
    
    private int amount;

    public Record(@NonNull int userId, @NonNull String type, @NonNull LocalDateTime date, @NonNull String category, String description, int amount) {
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }
}
