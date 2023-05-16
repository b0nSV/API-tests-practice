package site.nomoreparties.stellarburgers;

import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;
import site.nomoreparties.stellarburgers.steps.IngredientSteps;
import site.nomoreparties.stellarburgers.steps.OrderSteps;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import java.util.Optional;

import static org.apache.http.HttpStatus.SC_OK;

public class InitTests {
    static IngredientSteps ingredientSteps = new IngredientSteps();
    static OrderSteps orderSteps = new OrderSteps();
    static UserSteps userSteps = new UserSteps();

    static Optional<String> registerUserAndGetAccessToken(User user) {
        String accessToken = null;
        var registerUserResponse = userSteps.registerUser(user);
        if (registerUserResponse.statusCode() == SC_OK) accessToken = registerUserResponse.as(UserLoginResponse.class)
                .getAccessToken();
        return Optional.ofNullable(accessToken);
    }
}
