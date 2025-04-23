package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<Saving, String> {
    List<Saving> findAllByUserId(String userId);
}
