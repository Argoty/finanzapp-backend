package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.LoginRequest;
import com.finanzapp.app_financiera.dtos.ResponseMessage;
import com.finanzapp.app_financiera.dtos.UserDTO;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsuarios() {
        List<UserDTO> usuariosDto = userService.findAll();
        return ResponseEntity.ok(usuariosDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PostMapping("/registro")
    public ResponseEntity<UserDTO> signUpUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.signUpUser(user));
    }

    @PostMapping("/recuperar/{email}")
    public ResponseEntity<ResponseMessage> revocerAccount(@PathVariable String email) {
        return ResponseEntity.ok(userService.recoverAccount(email));
    }
}
