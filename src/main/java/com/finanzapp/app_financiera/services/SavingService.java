package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.repository.SavingRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import com.finanzapp.app_financiera.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Autowired
    public SavingService(SavingRepository savingRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.savingRepository = savingRepository;
        this.userRepository = userRepository;
        initSavings();
        this.jwtUtil = jwtUtil;
    }

    public void initSavings() {
        /*save(new Saving(1, "Laptop", 5000, 15000, "Inversión"));
        save(new Saving(1, "Vacaciones Europa", 20000, 100000, "Viajes"));
        save(new Saving(1, "Fondo Médico", 3000if(!(user.getId() == saving.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }, 20000, "Emergencia"));
        save(new Saving(1, "Cámara Profesional", 8000, 25000, "Entretenimiento"));
        save(new Saving(1, "Remodelación Hogar", 10000, 80000, "Vivienda"));
        save(new Saving(1, "Negocio Propio", 15000, 100000, "Inversión"));
        save(new Saving(1, "Concierto Internacional", 500, 5000, "Entretenimiento"));
        save(new Saving(1, "Viaje a Japón", 25000, 120000, "Viajes"));
        save(new Saving(1, "Curso de Idiomas", 4000, 12000, "Metas Personales"));
        save(new Saving(1, "Fondo de Emergencia", 20000, 50000, "Emergencia"));*/
    }

    public List<Saving> findAllSavings(String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return savingRepository.findAllByUserId(user.getId());
    }

    public Saving save(Saving saving, String token) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == saving.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido");
        }
        return savingRepository.save(saving);
    }

    public Saving deleteSavingById(int id, String token) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        savingRepository.deleteById(id);
        return saving.get();
    }

    public Saving updateSaving(Saving saving, String token, int id) {
        savingRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!(user.getId() == saving.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no valido");
        }

        saving.setId(id);
        return savingRepository.save(saving);
    }

    public Saving saveToSaving(int id, String token, double amount) {
        Optional<Saving> saving = savingRepository.findById(id);
        saving.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro"));

        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        saving.get().setAccumulatedAmount(amount + saving.get().getAccumulatedAmount());
        savingRepository.save(saving.get());
        return saving.get();
    }
}