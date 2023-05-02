package site.nomoreparties.stellarburgers.helpers.entities;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseAndToken {
    private Response response;
    private String authToken;
}
