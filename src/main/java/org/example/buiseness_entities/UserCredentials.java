package org.example.buiseness_entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentials {

    private String email;
    private String password;
    public UserCredentials(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
