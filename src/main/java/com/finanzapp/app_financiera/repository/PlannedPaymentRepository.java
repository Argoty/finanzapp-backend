package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.PlannedPayment;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PlannedPaymentRepository {

    private final Map<String, PlannedPayment> tablaPagos = new HashMap<>();

    public PlannedPayment save(PlannedPayment pago) {
        tablaPagos.put(pago.getId(), pago);
        return pago;
    }

    public PlannedPayment findById(String id) {
        return tablaPagos.get(id);
    }

    public void deleteById(String id) {
        tablaPagos.remove(id);
    }

    public PlannedPayment update(PlannedPayment pago) {
        if (tablaPagos.containsKey(pago.getId())) {
            tablaPagos.put(pago.getId(), pago);
            return pago;
        }
        return null;
    }

    public List<PlannedPayment> buscarPorFiltros(String userId, String query, String futurePeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return tablaPagos.values().stream()
                .filter(p -> p.getUserId().equals(userId))
                .filter(p -> p.getPaymentDate() == null)
                .filter(p -> futurePeriod == null || cumpleFiltroFecha(p.getDueDate(), futurePeriod)) // <- Filtra por últimos registros
                .filter(p -> (query == null || query.isEmpty())
                        || p.getType().toLowerCase().contains(query.toLowerCase())
                        || p.getCategory().toLowerCase().contains(query.toLowerCase())
                        || p.getName().toLowerCase().contains(query.toLowerCase())
                        || p.getDueDate().format(formatter).contains(query)
                        || String.valueOf(p.getAmount()).contains(query))
                
                .sorted((p1, p2) -> p2.getDueDate().compareTo(p1.getDueDate()))
                .collect(Collectors.toList());
    }
    private boolean cumpleFiltroFecha(LocalDate fecha, String futurePeriod) {
        LocalDate ahora = LocalDate.now();
        LocalDate fechaLimite = switch (futurePeriod.toLowerCase()) {
            case "1 semana" ->
                ahora.plusWeeks(1);
            case "1 mes" ->
                ahora.plusMonths(1);
            case "3 meses" ->
                ahora.plusWeeks(12);
            case "6 meses" ->
                ahora.plusMonths(6);
            case "1 año" ->
                ahora.plusYears(1);
            default ->
                null;
        };
        return fechaLimite == null || fecha.isBefore(fechaLimite);
    }
}
