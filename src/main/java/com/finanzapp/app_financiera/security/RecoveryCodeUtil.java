package com.finanzapp.app_financiera.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class RecoveryCodeUtil {
    private final String SECRET_KEY;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RecoveryCodeUtil(@Value("${RECOVERY_CODE_KEY}") String secretKey) {
        SECRET_KEY = secretKey;
    }

    public String generateRecoveryCode(String userName) {
        try {
            LocalDateTime expiration = LocalDateTime.now().plusMinutes(3);
            String data = userName + "::" + expiration.format(FORMATTER);
            return encrypt(data);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isRecoveryCodeValid(String recoveryCode) {
        try {
            String decrypted = decrypt(recoveryCode);
            String[] parts = decrypted.split("::");
            if (parts.length != 2) return false;

            LocalDateTime expirationTime = LocalDateTime.parse(parts[1], FORMATTER);
            return LocalDateTime.now().isBefore(expirationTime);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserNameFromCode(String recoveryCode) {
        try {
            String decrypted = decrypt(recoveryCode);
            return decrypted.split("::")[0];
        } catch (Exception e) {
            return null;
        }
    }

    private String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }
}

