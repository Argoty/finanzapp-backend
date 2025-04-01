package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Record;
import com.finanzapp.app_financiera.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

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
        save(new Record("Gasto", now, "Transporte", "Taxi", 7000));
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

    // Retorna todos los records
    public List<Record> findAll() {
        return recordRepository.findAll();
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
}
