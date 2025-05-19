package com.finanzapp.app_financiera.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
public class ValidateCodeRequest {

    public ValidateCodeRequest(@NonNull String email, @NonNull String recoveryCode) {
        this.email = email;
        this.recoveryCode = recoveryCode;
    }

    @NonNull
    String email;

    @NonNull
    String recoveryCode;
}
