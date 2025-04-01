package com.finanzapp.app_financiera.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class PaymentRecord {
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String plannedPaymentId;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate; // Fecha en que se realizó o se debía realizar el pago
}

