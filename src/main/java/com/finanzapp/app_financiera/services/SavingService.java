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
        /*save(new Saving("1","321", "Laptop", 5000, 15000, "Inversión"));
        save(new Saving("2","321", "Vacaciones Europa", 20000, 100000, "Viajes"));
        save(new Saving("3","321", "Fondo Médico", 3000, 20000, "Emergencia"));
        save(new Saving("4","321", "Cámara Profesional", 8000, 25000, "Entretenimiento"));
        save(new Saving("5","321", "Remodelación Hogar", 10000, 80000, "Vivienda"));
        save(new Saving("6","321", "Negocio Propio", 15000, 100000, "Inversión"));
        save(new Saving("7","321", "Concierto Internacional", 500, 5000, "Entretenimiento"));
        save(new Saving("8","321", "Viaje a Japón", 25000, 120000, "Viajes"));
        save(new Saving("9","321", "Curso de Idiomas", 4000, 12000, "Metas Personales"));
        save(new Saving("10","321", "Fondo de Emergencia", 20000, 50000, "Emergencia"));*/
    }

    public List<Saving> findAllSavings(String userId) {
        return savingRepository.findAllByUserId(userId);
    }

    public Saving save(Saving saving) {
        return savingRepository.save(saving);
    }

    public Saving deleteSavingById(String id) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        savingRepository.deleteById(id);
        return saving.get();
    }

    public Saving updateSaving(Saving saving, String userId, String id) {
        savingRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        saving.setId(id);
        return savingRepository.save(saving);
    }

    public Saving saveToSaving(String id, String userId, double amount) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        if (userRepository.findById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        saving.get().setAccumulatedAmount(amount + saving.get().getAccumulatedAmount());
        savingRepository.save(saving.get());
        return saving.get();
    }
}
