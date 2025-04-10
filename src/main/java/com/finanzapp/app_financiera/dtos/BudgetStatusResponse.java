package com.finanzapp.app_financiera.dtos;

import java.time.LocalDate;

import com.finanzapp.app_financiera.models.Budget;
import lombok.Data;

@Data
public class BudgetStatusResponse {
    private String id;
    private String category;
    private String name;
    private String period;
    private int limitAmount;
    private int totalSpent;
    private double percentageSpent;
    private int remainingAmount;
    private LocalDate startDate;

    public BudgetStatusResponse(Budget budget, int totalSpent, LocalDate startDate) {
        this.id = budget.getId();
        this.category = budget.getCategory();
        this.name = budget.getName();
        this.period = budget.getPeriod();
        this.limitAmount = budget.getLimitAmount();
        this.totalSpent = totalSpent;
        this.percentageSpent = (totalSpent / (double) budget.getLimitAmount()) * 100;
        this.remainingAmount = budget.getLimitAmount() - totalSpent;
        this.startDate = startDate;
    }
}


