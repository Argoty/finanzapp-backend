package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.CategoryDTO;
import com.finanzapp.app_financiera.dtos.MontoPeriodoDTO;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.repository.RecordRepository;
import com.finanzapp.app_financiera.repository.UserRepository;

import com.finanzapp.app_financiera.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecordService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public RecordService(RecordRepository recordRepository, UserRepository userRepository, JwtUtil jwtUtil) {
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
    
    private void validateTokenWithEntity(Record record, String token) {
        int userId = getUserIdWithToken(token);
        if (!(userId == record.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }
    }
    
    public Record save(Record record, String token) {
        validateTokenWithEntity(record, token);
        return recordRepository.save(record);
    }

    public Record findById(int id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Record con ID " + id + " no encontrado"));
    }

    public List<Record> buscarPorFiltros(String token, String query, String lastPeriod) {
        int userId = getUserIdWithToken(token);

        LocalDateTime limit = getDateLimit(lastPeriod);
        return recordRepository.findByUserIdAndFilters(userId, limit, query);
    }

    public Record update(int id, Record record, String token) {
        validateTokenWithEntity(record, token);
        findById(id);
        record.setId(id);
        return recordRepository.save(record);
    }

    public void deleteById(int id, String token) {
        Record record = findById(id);        
        validateTokenWithEntity(record, token);
        recordRepository.deleteById(id);
    }

    public List<CategoryDTO> obtenerTotalesPorCategory(String token, String lastPeriod) {
        int userId = getUserIdWithToken(token);
        // Calcula la fecha límite según el periodo seleccionado
        LocalDateTime limit = getDateLimit(lastPeriod);

        // Obtiene los gastos filtrados por usuario, tipo y fecha
        List<Record> gastos = (limit == null)
                ? recordRepository.findAllByUserIdAndTypeOrderByDateDesc(userId, "Gasto")
                : recordRepository.findAllByUserIdAndTypeAndDateGreaterThanEqualOrderByDateDesc(
                        userId, "Gasto", limit);

        // Agrupa los gastos por categoría y suma los montos
        Map<String, Integer> totals = gastos.stream()
                .collect(Collectors.groupingBy(Record::getCategory,
                        Collectors.summingInt(Record::getAmount)));

        // Convierte el resultado en una lista de CategoryDTO
        return totals.entrySet().stream()
                .map(e -> new CategoryDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<MontoPeriodoDTO> obtenerBucketsPorPeriodo(String token, String lastPeriod) {
        int userId = getUserIdWithToken(token);
        
        LocalDate today = LocalDate.now();
        LocalDate start;
        int buckets;

        // Define el rango de fechas y cantidad de buckets según el periodo
        switch (lastPeriod.toLowerCase()) {
            case "1 semana":
                start = today.minusDays(6);
                buckets = 7;
                break;
            case "1 mes":
                start = today.minusMonths(1);
                buckets = 4;
                break;
            case "3 meses":
                start = today.minusMonths(3);
                buckets = 6;
                break;
            case "6 meses":
                start = today.minusMonths(6);
                buckets = 6;
                break;
            case "1 año":
                start = today.minusYears(1);
                buckets = 6;
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Periodo no válido");
        }

        // Obtiene los registros desde la fecha de inicio
        LocalDateTime limit = start.atStartOfDay();
        List<Record> records = recordRepository.findByUserIdAndFilters(userId, limit, null);

        // Genera los buckets de datos para el periodo
        return generarBuckets(records, start, today, buckets);
    }

    private List<MontoPeriodoDTO> generarBuckets(List<Record> records,
            LocalDate start, LocalDate end, int desiredBuckets) {
        // Calcula el número total de días y el tamaño base de cada bucket
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        long baseSize = days / desiredBuckets;
        long rem = days % desiredBuckets;

        LocalDate cursor = start;
        List<MontoPeriodoDTO> resultado = new java.util.ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_DATE;

        // Recorre y crea cada bucket
        for (int i = 0; i < desiredBuckets; i++) {
            long bucketSize = baseSize + (i < rem ? 1 : 0); // Distribuye días sobrantes
            LocalDate bucketEnd = cursor.plusDays(bucketSize);
            LocalDate bucketStart = cursor;
            String label = cursor.format(iso) + " - " + bucketEnd.minusDays(1).format(iso);

            // Calcula ingresos en el bucket actual
            int ingresos = records.stream()
                    .filter(r -> !r.getDate().toLocalDate().isBefore(bucketStart)
                    && r.getDate().toLocalDate().isBefore(bucketEnd)
                    && r.getType().equalsIgnoreCase("Ingreso"))
                    .mapToInt(Record::getAmount)
                    .sum();

            // Calcula gastos en el bucket actual
            int gastos = records.stream()
                    .filter(r -> !r.getDate().toLocalDate().isBefore(bucketStart)
                    && r.getDate().toLocalDate().isBefore(bucketEnd)
                    && r.getType().equalsIgnoreCase("Gasto"))
                    .mapToInt(Record::getAmount)
                    .sum();

            // Agrega ingresos y gastos al resultado
            resultado.add(new MontoPeriodoDTO(label, ingresos, "Ingreso"));
            resultado.add(new MontoPeriodoDTO(label, gastos, "Gasto"));

            cursor = bucketEnd; // Mueve el cursor al siguiente bucket
        }

        return resultado;
    }

    private LocalDateTime getDateLimit(String period) {
        LocalDateTime now = LocalDateTime.now();
        if (period == null) {
            return null;
        }
        return switch (period.toLowerCase()) {
            case "1 semana" ->
                now.minusWeeks(1);
            case "1 mes" ->
                now.minusMonths(1);
            case "3 meses" ->
                now.minusMonths(3);
            case "6 meses" ->
                now.minusMonths(6);
            case "1 año" ->
                now.minusYears(1);
            default ->
                null;
        };
    }

}
