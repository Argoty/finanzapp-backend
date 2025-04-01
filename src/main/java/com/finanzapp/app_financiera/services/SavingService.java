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
        save(
                new Saving("",10000, 300000, "Saving universidad"));
        save(new Saving("",0, 2000, "Saving paseo"));
        save(new Saving("",200000, 500000, "Saving casa"));
    }

    public List<Saving> findAllSavings() {
        return savingRepository.findAllSavings();
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
