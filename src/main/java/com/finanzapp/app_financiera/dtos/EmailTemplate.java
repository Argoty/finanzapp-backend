package com.finanzapp.app_financiera.dtos;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EmailTemplate {

    private String subject;
    private String to;
    private Map<String, Object> dataTemplate = new HashMap<>();
    private String templateName;

    public EmailTemplate(String subject, String to, Map<String, Object> data, String templateName) {
        this.subject = subject;
        this.to = to;
        this.dataTemplate = data;
        this.templateName = templateName;
    }

}
