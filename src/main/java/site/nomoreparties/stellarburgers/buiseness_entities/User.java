package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String email;
    private String password;
    private String name;
}
