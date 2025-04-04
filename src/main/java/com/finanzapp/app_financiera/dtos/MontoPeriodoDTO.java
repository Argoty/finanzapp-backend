package com.finanzapp.app_financiera.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MontoPeriodoDTO {
    private String periodo;
    private int monto;
    private String tipo;
}

