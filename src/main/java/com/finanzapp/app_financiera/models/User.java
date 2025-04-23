package com.finanzapp.app_financiera.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;
}
