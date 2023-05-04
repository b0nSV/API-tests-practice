package site.nomoreparties.stellarburgers.buiseness_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginResponse {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
