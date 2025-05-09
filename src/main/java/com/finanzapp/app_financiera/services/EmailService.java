package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.EmailTemplate;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private RestTemplate restTemplate;
    private static final Dotenv dotenv = Dotenv.load();
    private String url = dotenv.get("EMAILSERVICE_URL");

    public void sendRecoveringEmail(String subject, String to, String name, String password) {
        EmailTemplate template = new EmailTemplate(subject, "cruz.simon.4962@eam.edu.co",
                new HashMap<>(){{put("name",name); put("email",to); put("password",password);}}, "registro.html");

        restTemplate.postForEntity(url, template, String.class);
    }
}
