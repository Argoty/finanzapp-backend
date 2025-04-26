package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.repository.SavingRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SavingService {

    private final SavingRepository savingRepository;
    private final UserRepository userRepository;

    @Autowired
    public SavingService(SavingRepository savingRepository, UserRepository userRepository) {
        this.savingRepository = savingRepository;
        this.userRepository = userRepository;
        initSavings();
    }

    public void initSavings() {
        /*save(new Saving(1, "Laptop", 5000, 15000, "Inversión"));
        save(new Saving(1, "Vacaciones Europa", 20000, 100000, "Viajes"));
        save(new Saving(1, "Fondo Médico", 3000, 20000, "Emergencia"));
        save(new Saving(1, "Cámara Profesional", 8000, 25000, "Entretenimiento"));
        save(new Saving(1, "Remodelación Hogar", 10000, 80000, "Vivienda"));
        save(new Saving(1, "Negocio Propio", 15000, 100000, "Inversión"));
        save(new Saving(1, "Concierto Internacional", 500, 5000, "Entretenimiento"));
        save(new Saving(1, "Viaje a Japón", 25000, 120000, "Viajes"));
        save(new Saving(1, "Curso de Idiomas", 4000, 12000, "Metas Personales"));
        save(new Saving(1, "Fondo de Emergencia", 20000, 50000, "Emergencia"));*/
    }

    public List<Saving> findAllSavings(int userId) {
        return savingRepository.findAllByUserId(userId);
    }

    public Saving save(Saving saving) {
        return savingRepository.save(saving);
    }

    public Saving deleteSavingById(int id) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        savingRepository.deleteById(id);
        return saving.get();
    }

    public Saving updateSaving(Saving saving, int userId, int id) {
        savingRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        userRepository.findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        saving.setId(id);
        return savingRepository.save(saving);
    }

    public Saving saveToSaving(int id, int userId, double amount) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        saving.get().setAccumulatedAmount(amount + saving.get().getAccumulatedAmount());
        savingRepository.save(saving.get());
        return saving.get();
    }
}
