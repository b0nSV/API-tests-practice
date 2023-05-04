package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessageResponse {
    private boolean success;
    private String message;
}
