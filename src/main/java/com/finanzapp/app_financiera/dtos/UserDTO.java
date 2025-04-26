package com.finanzapp.app_financiera.dtos;

import com.finanzapp.app_financiera.models.User;
import lombok.Data;

@Data
public class UserDTO {

    private int id;
    private String username;
    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
