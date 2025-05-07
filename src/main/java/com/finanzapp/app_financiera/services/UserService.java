package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.dtos.ResponseMessage;
import com.finanzapp.app_financiera.dtos.UserDTO;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        initSampleData();
    }
    
    private void initSampleData() {
        /*save(new User("pepe", "pepe", "pepe"));
        save(new User("maría", "maria@example.com", "lol"));
        save(new User("carlos", "carlos@example.com", "err"));*/
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(int id) {
        Optional<User> user = userRepository.findById(id);
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

    public UserDTO loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO"));

        if(!user.get().getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }
        return new UserDTO(user.get());
    }

    public UserDTO signUpUser(User user) {
        if(!user.getEmail().contains("@")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EL CORREO DEBE SER VALIDO");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ESTE CORREO YA ESTA REGISTRADO");
        }
        if(user.getPassword().length() < 8){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"LA CONTRASEÑA DEBE TENER AL MENOS 8 CARACTERES");
        }
        if(user.getUsername().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DEBE TENER UN USUARIO");
        }

        return new UserDTO(save(user));
    }

    public ResponseMessage recoverAccount(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE CORREO NO ESTA REGISTRADO"));

        emailService.sendRecoveringEmail("RECUPERACION DE CUENTA", email,
                user.get().getUsername(), user.get().getPassword());

        return new ResponseMessage("Se envio un correo de recuperación");
    }

}