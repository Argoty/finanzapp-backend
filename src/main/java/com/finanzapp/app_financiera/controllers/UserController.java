package com.finanzapp.app_financiera.controllers;

import com.finanzapp.app_financiera.dtos.LoginRequest;
import com.finanzapp.app_financiera.dtos.ResponseMessage;
import com.finanzapp.app_financiera.dtos.UserDTO;
import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para la gestión de usuarios")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener todos los usuarios registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
        @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsuarios() {
        List<UserDTO> usuariosDto = userService.findAll();
        return ResponseEntity.ok(usuariosDto);
    }

    @Operation(summary = "Iniciar sesión de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
        @ApiResponse(responseCode = "401", description = "Contraseña incorrecta"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(
            @Parameter(description = "Credenciales de inicio de sesión", required = true)
            @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (correo no válido, contraseña corta o usuario vacío)"),
        @ApiResponse(responseCode = "409", description = "Correo ya registrado")
    })
    @PostMapping("/registro")
    public ResponseEntity<UserDTO> signUpUser(
            @Parameter(description = "Datos del nuevo usuario", required = true)
            @RequestBody User user) {
        return ResponseEntity.ok(userService.signUpUser(user));
    }

    @Operation(summary = "Recuperar cuenta de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Correo de recuperación enviado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/recuperar/{email}")
    public ResponseEntity<ResponseMessage> revocerAccount(
            @Parameter(description = "Correo electrónico del usuario", required = true)
            @PathVariable String email) {
        return ResponseEntity.ok(userService.recoverAccount(email));
    }
}
