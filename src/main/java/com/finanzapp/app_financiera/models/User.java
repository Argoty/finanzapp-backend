package com.finanzapp.app_financiera.models;

import lombok.*;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class User {
    private String id = UUID.randomUUID().toString();
    @NonNull private String username;
    @NonNull private String email;
    @NonNull private String password;
    
}
