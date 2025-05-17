package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.EmailTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

@Service
public class EmailService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${emailservice.url}")
    private String url;

    public void sendRecoveringEmail(String subject, String to, String name, String recoveryCode) {
        EmailTemplate template = new EmailTemplate(subject, "cruz.simon.4962@eam.edu.co",
                new HashMap<>(){{put("name",name); put("codigo",recoveryCode); }}, "registro.html");

        restTemplate.postForEntity(url, template, String.class);
    }
}
