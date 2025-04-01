package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {

    private final Map<String, User> tablaUsuarios = new HashMap<>();

    public User save(User User) {
        tablaUsuarios.put(User.getId(), User);
        return User;
    }

    public User findById(String id) {
        return tablaUsuarios.get(id);
    }

    public User findByEmail(String email) {
        for (User user : tablaUsuarios.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return new ArrayList<>(tablaUsuarios.values());
    }

    public void deleteById(String id) {
        tablaUsuarios.remove(id);
    }

    public User update(User User) {
        if (tablaUsuarios.containsKey(User.getId())) {
            tablaUsuarios.put(User.getId(), User);
            return User;
        }
        return null;
    }


}
