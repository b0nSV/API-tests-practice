package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.Data;

@Data
public class UserPatchResponse {
    private boolean success;
    private User user;
}
