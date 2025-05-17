package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.PlannedPayment;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.models.User;

import com.finanzapp.app_financiera.repository.PlannedPaymentRepository;
import com.finanzapp.app_financiera.repository.RecordRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import com.finanzapp.app_financiera.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlannedPaymentService {

    private final PlannedPaymentRepository pagoRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public PlannedPaymentService(PlannedPaymentRepository pagoRepository,
                                 RecordRepository recordRepository,UserRepository userRepository, JwtUtil jwtUtil) {
        this.pagoRepository = pagoRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    
    private int getUserIdWithToken(String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return user.getId();
    }

    private void validateTokenWithEntity(PlannedPayment plannedPayment, String token) {
        int userId = getUserIdWithToken(token);
        if (!(userId == plannedPayment.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }
    }
    public PlannedPayment save(PlannedPayment pago, String token) {
        validateTokenWithEntity(pago, token);
        return pagoRepository.save(pago);
    }

    public PlannedPayment findById(int id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pago con ID " + id + " no encontrado"));
    }

    public List<PlannedPayment> buscarPorFiltros(String token, String query, String futurePeriod) {
        int userId = getUserIdWithToken(token);
        LocalDate limitDate = calcularFechaLimite(futurePeriod);
        return pagoRepository.findByUserIdAndFilters(userId, limitDate, query);
    }

    public PlannedPayment update(int id, PlannedPayment pago, String token) {
        validateTokenWithEntity(pago, token);
        findById(id);
        pago.setId(id);
        return pagoRepository.save(pago);
    }

    public PlannedPayment confirmarPago(int id, String token) {
        PlannedPayment pp = findById(id);
        validateTokenWithEntity(pp, token);
        
        if (pp.getPaymentDate() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pago con ID " + id + " ya fue confirmado");
        }
        pp.setPaymentDate(LocalDate.now());
        pagoRepository.save(pp);
        recordRepository.save(new Record(
                pp.getUserId(), pp.getType(), LocalDateTime.now(),
                pp.getCategory(), pp.getName(), pp.getAmount()
        ));
        return pp;
    }

    public void deleteById(int id, String token) {
        PlannedPayment pp = findById(id);
        validateTokenWithEntity(pp, token);
        pagoRepository.deleteById(id);
    }

    private LocalDate calcularFechaLimite(String futurePeriod) {
        LocalDate hoy = LocalDate.now();
        if (futurePeriod == null) return null;
        return switch (futurePeriod.toLowerCase()) {
            case "1 semana" -> hoy.plusWeeks(1);
            case "1 mes"    -> hoy.plusMonths(1);
            case "3 meses"  -> hoy.plusMonths(3);
            case "6 meses"  -> hoy.plusMonths(6);
            case "1 año"    -> hoy.plusYears(1);
            default           -> null;
        };
    }
}

