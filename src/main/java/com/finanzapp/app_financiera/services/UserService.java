package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.AuthResponse;
import com.finanzapp.app_financiera.dtos.ResponseMessage;
import com.finanzapp.app_financiera.dtos.UserDTO;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.repository.UserRepository;
import com.finanzapp.app_financiera.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        initSampleData();
        this.jwtUtil = jwtUtil;
    }

    private void initSampleData() {
        //save(new User("pepe", "pepe", passwordEncoder.encode("pepe")));
        //save(new User("maría", "maria@example.com", passwordEncoder.encode("lol")));
        //save(new User("carlos", "carlos@example.com", passwordEncoder.encode("err")));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(int id) {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO"));
        return user.get();
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO"));
        return user.get();
    }

    public List<UserDTO> findAll() {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDTOs.add(new UserDTO(user));
        }
        return userDTOs;
    }

    public User update(User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO"));
        userRepository.save(user);
        return user;
    }

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    public AuthResponse loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO"));

        if(!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }
        String accessToken = jwtUtil.generateAccessToken(user.get());
        return new AuthResponse(accessToken, new UserDTO(user.get()));
    }

    public AuthResponse signUpUser(User user) {
        if(!user.getEmail().contains("@")){
            System.out.println("--->>>>1");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EL CORREO DEBE SER VALIDO");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            System.out.println("--->>>>2");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ESTE CORREO YA ESTA REGISTRADO");
        }
        if(user.getPassword().length() < 8){
            System.out.println("--->>>>3");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"LA CONTRASEÑA DEBE TENER AL MENOS 8 CARACTERES");
        }
        if(user.getUsername().isEmpty()){
            System.out.println("--->>>>4");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DEBE TENER UN USUARIO");
        }
        String accessToken = jwtUtil.generateAccessToken(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        return new AuthResponse(accessToken, new UserDTO(user));
    }

    public ResponseMessage recoverAccount(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE CORREO NO ESTA REGISTRADO"));

        String recoveryCode = jwtUtil.generateRecoveryToken(user.get());
        recoveryCode = passwordEncoder.encode(recoveryCode);

        emailService.sendRecoveringEmail("RECUPERACION DE CUENTA", email,
                user.get().getUsername(), recoveryCode);

        return new ResponseMessage("Se envio un correo de recuperación");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findByEmail(email);
    }

    public String validateRecoveryToken(String recoveryToken) {
        Optional<String> email = jwtUtil.extractEmailFromRecoveryToken(recoveryToken);
                email.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));

        Optional<User> user = userRepository.findByEmail(email.get());
            user.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));

        jwtUtil.validateRecoveryToken(recoveryToken, user.get());
        return jwtUtil.generateAccessToken(user.get());
    }

    public void changePassword(String token, String newPassword) {
        Optional<String> email = jwtUtil.extractEmailFromAccessToken(token);
            email.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));

        Optional<User> user = userRepository.findByEmail(email.get());
            user.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));

        user.get().setPassword(passwordEncoder.encode(newPassword));
        save(user.get());
    }
}