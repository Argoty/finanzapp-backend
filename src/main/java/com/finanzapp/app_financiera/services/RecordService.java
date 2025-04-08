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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

        save(new Record("321", "Gasto", now, "Transporte", "Pasaje de bus", 5000));
        save(new Record("321", "Gasto", now.minusDays(1), "Transporte", "Taxi", 7000));
        save(new Record("321", "Gasto", now.minusDays(3), "Alimentación", "Cena en restaurante", 45000));
        save(new Record("321", "Gasto", now.minusDays(5), "Alimentación", "Mercado", 30000));
        save(new Record("321", "Gasto", now.minusDays(10), "Tecnología", "Auriculares inalámbricos", 120000));
        save(new Record("321", "Gasto", now.minusDays(4), "Deportes y Fitness", "Membresía gimnasio", 80000));

        // Datos de ingresos (no influyen en el estado del presupuesto, ya que se filtran solo "Gasto")
        save(new Record("321", "Ingreso", now.minusDays(15), "Salario", "Pago mensual", 3500000));
        save(new Record("321", "Ingreso", now.minusDays(12), "Freelance o trabajos extra", "Diseño web", 500000));
        save(new Record("321", "Ingreso", now.minusDays(8), "Inversiones", "Dividendos acciones", 250000));
        save(new Record("321", "Ingreso", now.minusDays(6), "Alquileres", "Renta apartamento", 1200000));
        save(new Record("321", "Ingreso", now.minusDays(2), "Regalos o bonos", "Bono por desempeño", 300000));
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
    public List<Record> buscarPorFiltros(String userId, String query, String lastPeriod) {
        return recordRepository.buscarPorFiltros(userId, query, lastPeriod);
    }

    // Actualiza un record. Si no existe, se lanza una excepción.
    public Record update(String id, Record record) {
        findById(id); // Lanza excepción si no se encuentra
        record.setId(id);
        return recordRepository.update(record);
    }

    // Elimina un record. Si no existe, se lanza una excepción.
    public void deleteById(String id, String userId) {
        Record record = findById(id); // Lanza excepción si no se encuentra
        if (!record.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "el id del usuario no es el mismo que el que creo este record");
        }
        recordRepository.deleteById(id);
    }

    public List<CategoryDTO> obtenerTotalesPorCategory(String userId, String lastPeriod) {
        // Se obtienen los registros filtrados según el período
        List<Record> gastos = recordRepository.buscarPorFiltros(userId, "Gasto", lastPeriod);

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

    public List<MontoPeriodoDTO> obtenerBucketsPorPeriodo(String userId, String lastPeriod) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        int desiredBuckets;

        switch (lastPeriod.toLowerCase()) {
            case "1 semana":
                // Para 7 días (incluyendo hoy), el inicio es hoy menos 6 días.
                startDate = today.minusDays(6);
                desiredBuckets = 7;
                break;
            case "1 mes":
                // Para 1 mes, queremos 4 buckets (aproximadamente semanas).
                startDate = today.minusMonths(1);
                desiredBuckets = 4;
                break;
            case "3 meses":
                // Para 3 meses, usar 6 buckets (cada 2 semanas, aproximadamente).
                startDate = today.minusMonths(3);
                desiredBuckets = 6;
                break;
            case "6 meses":
                // Para 6 meses, usar 6 buckets (cada mes).
                startDate = today.minusMonths(6);
                desiredBuckets = 6;
                break;
            case "1 año":
                // Para 1 año, usar 6 buckets (cada 2 meses).
                startDate = today.minusYears(1);
                desiredBuckets = 6;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Periodo no válido");
        }

        // Obtener los registros filtrados según el lastPeriod
        List<Record> records = recordRepository.buscarPorFiltros(userId, null, lastPeriod);

        return generarBuckets(records, startDate, today, desiredBuckets);
    }

    private List<MontoPeriodoDTO> generarBuckets(List<Record> records, LocalDate startDate, LocalDate endDate, int desiredBuckets) {
        List<MontoPeriodoDTO> resultado = new ArrayList<>();

        // Total de días entre startDate y endDate (incluyendo ambos extremos)
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Tamaño base de cada bucket
        long baseBucketSize = totalDays / desiredBuckets;
        // Resto que se reparte en los primeros buckets
        long remainder = totalDays % desiredBuckets;

        LocalDate current = startDate;
        for (int i = 0; i < desiredBuckets; i++) {
            // Cada bucket tendrá baseBucketSize días, y si i < remainder, se le suma 1 día extra
            long currentBucketSize = baseBucketSize + (i < remainder ? 1 : 0);
            LocalDate bucketEnd = current.plusDays(currentBucketSize);

            // Asigna current a una variable final para usarla en la lambda
            final LocalDate currentBucketStart = current;

            // Generar etiqueta para el bucket: de currentBucketStart hasta bucketEnd - 1
            String etiqueta = currentBucketStart.format(DateTimeFormatter.ISO_DATE)
                    + " - " + bucketEnd.minusDays(1).format(DateTimeFormatter.ISO_DATE);

            // Filtrar registros dentro del intervalo [currentBucketStart, bucketEnd)
            List<Record> registrosFiltrados = records.stream()
                    .filter(r -> {
                        LocalDate recordDate = r.getDate().toLocalDate();
                        return !recordDate.isBefore(currentBucketStart) && recordDate.isBefore(bucketEnd);
                    })
                    .collect(Collectors.toList());

            int montoIngreso = registrosFiltrados.stream()
                    .filter(r -> r.getType().equalsIgnoreCase("Ingreso"))
                    .mapToInt(Record::getAmount)
                    .sum();
            int montoGasto = registrosFiltrados.stream()
                    .filter(r -> r.getType().equalsIgnoreCase("Gasto"))
                    .mapToInt(Record::getAmount)
                    .sum();

            resultado.add(new MontoPeriodoDTO(etiqueta, montoIngreso, "Ingreso"));
            resultado.add(new MontoPeriodoDTO(etiqueta, montoGasto, "Gasto"));

            // Actualizar current para el siguiente bucket
            current = bucketEnd;
        }

        return resultado;
    }

}
