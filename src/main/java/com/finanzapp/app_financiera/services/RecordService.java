package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.CategoryDTO;
import com.finanzapp.app_financiera.dtos.MontoPeriodoDTO;
import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
        initSampleData();
    }

    // Inicialización de datos de ejemplo
    private void initSampleData() {
        LocalDateTime now = LocalDateTime.now();

        save(new Record("Gasto", now, "Transporte", "Pasaje de bus", 5000));
        save(new Record("Gasto", now.minusDays(1), "Transporte", "Taxi", 7000));
        save(new Record("Gasto", now.minusDays(3), "Alimentación", "Cena en restaurante", 45000));
        save(new Record("Gasto", now.minusDays(5), "Alimentación", "Mercado", 30000));
        save(new Record("Gasto", now.minusDays(10), "Tecnología", "Auriculares inalámbricos", 120000));
        save(new Record("Gasto", now.minusDays(4), "Deportes y Fitness", "Membresía gimnasio", 80000));

        // Datos de ingresos (no influyen en el estado del presupuesto, ya que se filtran solo "Gasto")
        save(new Record("Ingreso", now.minusDays(15), "Salario", "Pago mensual", 3500000));
        save(new Record("Ingreso", now.minusDays(12), "Freelance o trabajos extra", "Diseño web", 500000));
        save(new Record("Ingreso", now.minusDays(8), "Inversiones", "Dividendos acciones", 250000));
        save(new Record("Ingreso", now.minusDays(6), "Alquileres", "Renta apartamento", 1200000));
        save(new Record("Ingreso", now.minusDays(2), "Regalos o bonos", "Bono por desempeño", 300000));
    }

    // Agregar un record (aquí podrías validar el token/rol de admin si fuera necesario)
    public Record agregarRecord(Record record) {
        return save(record);
    }

    // Guarda el record en el repositorio
    public Record save(Record record) {
        return recordRepository.save(record);
    }

    // Busca un record por ID y lanza excepción si no existe
    public Record findById(String id) {
        Record record = recordRepository.findById(id);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record con ID " + id + " no encontrado");
        }
        return record;
    }

    // Busca records usando un query de búsqueda y un filtro de tiempo (por ejemplo: \"1 semana\", \"1 mes\", etc.)
    public List<Record> buscarPorFiltros(String query, String lastPeriod) {
        return recordRepository.buscarPorFiltros(query, lastPeriod);
    }

    // Actualiza un record. Si no existe, se lanza una excepción.
    public Record update(String id, Record record) {
        findById(id); // Lanza excepción si no se encuentra
        record.setId(id);
        return recordRepository.update(record);
    }

    // Elimina un record. Si no existe, se lanza una excepción.
    public void deleteById(String id) {
        findById(id); // Lanza excepción si no se encuentra
        recordRepository.deleteById(id);
    }

    public List<CategoryDTO> obtenerTotalesPorCategory(String lastPeriod) {
        // Se obtienen los registros filtrados según el período
        List<Record> gastos = recordRepository.buscarPorFiltros("Gasto", lastPeriod);

        // Agrupamos por categoría y sumamos los montos
        Map<String, Integer> totalesPorCategory = gastos.stream()
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.summingInt(Record::getAmount)
                ));
        // Convertimos el mapa en una lista de DTOs
        return totalesPorCategory.entrySet().stream()
                .map(entry -> new CategoryDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<MontoPeriodoDTO> obtenerBucketsPorPeriodo(String lastPeriod) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate endDate = ahora.toLocalDate();
        LocalDate startDate;
        Period binPeriod;

        switch (lastPeriod.toLowerCase()) {
            case "1 semana":
                startDate = ahora.minusWeeks(1).toLocalDate();
                binPeriod = Period.ofDays(1); // buckets diarios
                break;
            case "1 mes":
                startDate = ahora.minusMonths(1).toLocalDate();
                binPeriod = Period.ofWeeks(1); // buckets semanales (aprox. 4 semanas)
                break;
            case "3 meses":
                startDate = ahora.minusMonths(3).toLocalDate();
                binPeriod = Period.ofDays(14); // buckets de 2 semanas
                break;
            case "6 meses":
                startDate = ahora.minusMonths(6).toLocalDate();
                binPeriod = Period.ofMonths(1); // buckets mensuales
                break;
            case "1 año":
                startDate = ahora.minusYears(1).toLocalDate();
                binPeriod = Period.ofMonths(2); // buckets de 2 meses
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Periodo no válido");
        }

        // Obtener los registros filtrados según el lastPeriod
        // Reutilizamos el método buscarPorFiltros que ya tienes
        List<Record> records = recordRepository.buscarPorFiltros(null, lastPeriod);

        return generarBuckets(records, startDate, endDate, binPeriod);
    }

    private List<MontoPeriodoDTO> generarBuckets(List<Record> records, LocalDate startDate, LocalDate endDate, Period binPeriod) {
        List<MontoPeriodoDTO> resultado = new ArrayList<>();
        LocalDate binStart = startDate;

        while (binStart.isBefore(endDate)) {
            final LocalDate currentBinStart = binStart;
            LocalDate binEnd = binStart.plus(binPeriod);

            // Generar etiqueta para el bucket, puedes formatearla a tu gusto.
            String etiqueta = currentBinStart.toString() + " - " + binEnd.minusDays(1).toString();

            // Filtrar registros dentro del bucket
            List<Record> registrosFiltrados = records.stream()
                    .filter(r -> {
                        LocalDate recordDate = r.getDate().toLocalDate();
                        return !recordDate.isBefore(currentBinStart) && recordDate.isBefore(binEnd);
                    })
                    .collect(Collectors.toList());

            // Sumar montos para ingresos
            int montoIngreso = registrosFiltrados.stream()
                    .filter(r -> r.getType().equalsIgnoreCase("Ingreso"))
                    .mapToInt(Record::getAmount)
                    .sum();

            // Sumar montos para gastos
            int montoGasto = registrosFiltrados.stream()
                    .filter(r -> r.getType().equalsIgnoreCase("Gasto"))
                    .mapToInt(Record::getAmount)
                    .sum();

            resultado.add(new MontoPeriodoDTO(etiqueta, montoIngreso, "Ingreso"));

            resultado.add(new MontoPeriodoDTO(etiqueta, montoGasto, "Gasto"));

            binStart = binEnd;
        }

        return resultado;
    }
}
