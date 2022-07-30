package org.example.steps;
import io.qameta.allure.Step;
import org.example.buiseness_entities.IngredientsResponse;

import static io.restassured.RestAssured.given;
import static org.example.steps.BaseApiSpecs.BASE_URL;
import static org.example.steps.BaseApiSpecs.getGetReqSpec;

public class IngredientSteps {

    public static final String GET_INGREDIENTS_URL = "/ingredients";

    @Step("Получить доступные ингредиенты")
    public static IngredientsResponse getIngredients () {
        return given()
                .spec(getGetReqSpec())
                .when()
                .get(BASE_URL + GET_INGREDIENTS_URL).as(IngredientsResponse.class);
    }
}
