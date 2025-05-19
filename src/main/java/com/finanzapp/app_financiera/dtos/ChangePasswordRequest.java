package com.finanzapp.app_financiera.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {

    @NonNull
    String email;

    @NonNull
    String recoveryCode;

    @NonNull
    String newPassword;
}
