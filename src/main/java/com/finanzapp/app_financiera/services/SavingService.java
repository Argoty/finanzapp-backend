package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.Saving;
import com.finanzapp.app_financiera.repository.SavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavingService {

    private final SavingRepository savingRepository;

    @Autowired
    public SavingService(SavingRepository savingRepository) {
        this.savingRepository = savingRepository;
        initSavings();
    }

    public void initSavings(){
        save(new Saving("123","Universidad",10000, 300000, "Emergencia"));
        save(new Saving("123","Casa",1000, 2000, "Metas Personales"));
        save(new Saving("123","Banco",200000, 500000, "Vivienda"));
        save(new Saving("123","Universidad",10000, 300000, "Inversión"));
        save(new Saving("123","Casa",1000, 2000, "Entretenimiento"));
        save(new Saving("123","Banco",200000, 500000, "Viajes"));
        save(new Saving("123","Universidad",10000, 300000, "Saving"));
    }

    public List<Saving> findAllSavings(String userId) {
        return savingRepository.findAllSavings(userId);
    }



    public Saving save(Saving saving) {
        return savingRepository.save(saving);
    }

    public Saving deleteSavingById(String id) {
        if(savingRepository.findSavingById(id) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro este ahorro");
        }
        return savingRepository.remove(id);
    }
}
