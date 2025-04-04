package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Record;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RecordRepository {

    private final Map<String, Record> tablaRegistros = new HashMap<>();

    public Record save(Record record) {
        tablaRegistros.put(record.getId(), record);
        return record;
    }

    public Record findById(String id) {
        return tablaRegistros.get(id);
    }

    public List<Record> findAll() {
        return new ArrayList<>(tablaRegistros.values());
    }

    public void deleteById(String id) {
        tablaRegistros.remove(id);
    }

    public Record update(Record record) {
        if (tablaRegistros.containsKey(record.getId())) {
            tablaRegistros.put(record.getId(), record);
            return record;
        }
        return null;
    }

    public List<Record> buscarPorFiltros(String userId,String buscador, String filtroFecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return tablaRegistros.values().stream()
                .filter(e -> e.getUserId().equals(userId))
                .filter(record -> buscador == null
                || (record.getType().toLowerCase().contains(buscador.toLowerCase()))
                || (record.getDescription() != null && record.getDescription().toLowerCase().contains(buscador.toLowerCase()))
                || (record.getCategory().toLowerCase().contains(buscador.toLowerCase()))
                || String.valueOf(record.getAmount()).contains(buscador)
                || record.getDate().format(formatter).contains(buscador)) // <- Busca por fecha u hora
                .filter(record -> filtroFecha == null || cumpleFiltroFecha(record.getDate(), filtroFecha)) // <- Filtra por últimos registros
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate())) // Ordena de más reciente a más antiguo
                .collect(Collectors.toList());
    }

    private boolean cumpleFiltroFecha(LocalDateTime fecha, String filtroFecha) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = switch (filtroFecha.toLowerCase()) {
            case "1 semana" ->
                ahora.minusWeeks(1);
            case "1 mes" ->
                ahora.minusMonths(1);
            case "3 meses" ->
                ahora.minusWeeks(12);
            case "6 meses" ->
                ahora.minusMonths(6);
            case "1 año" ->
                ahora.minusYears(1);
            default ->
                null;
        };
        return fechaLimite == null || fecha.isAfter(fechaLimite);
    }

}
