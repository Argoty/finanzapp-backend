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

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

        initSampleData();
    }
    
    private void initSampleData() {
        User userDefault = new User("pepe", "pepe", "pepe");
        userDefault.setId("321");
        save(userDefault);
        save(new User("maría", "maria@example.com", "lol"));
        save(new User("carlos", "carlos@example.com", "err"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(String id) {
        return userRepository.findById(id);
    }

    public List<UserDTO> findAll() {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDTOs.add(new UserDTO(user));
        }
        return userDTOs;
    }

    public User update(User user) {
        return userRepository.update(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user != null && user.getPassword().equals(password)){
            return new UserDTO(user);
        }
        if(user != null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE USUARIO NO ESTA REGISTRADO");
    }

    public UserDTO signUpUser(User user) {
        if(!user.getEmail().contains("@")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EL CORREO DEBE SER VALIDO");
        }
        if(userRepository.findByEmail(user.getEmail()) != null){
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
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ESTE CORREO NO ESTA REGISTRADO");
        }
        EmailService.enviarCorreo("RECUPERACION DE CUENTA PARA: " + user.getUsername(),
                "EMAIL: " + email + "\nCONTRASEÑA: " + user.getPassword());

        return new ResponseMessage("Se envio un correo de recuperación");
    }

}
