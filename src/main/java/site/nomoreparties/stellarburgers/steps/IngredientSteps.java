package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import site.nomoreparties.stellarburgers.BaseApiSpecs;
import site.nomoreparties.stellarburgers.buiseness_entities.Ingredient;
import site.nomoreparties.stellarburgers.buiseness_entities.IngredientsResponse;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class IngredientSteps extends BaseApiSpecs {

    public static final String GET_INGREDIENTS_URL = "/ingredients";

    @Step("Получить доступные ингредиенты")
    public Optional<List<Ingredient>> getIngredients() {
        var response = given()
                .spec(getReqSpec())
                .when()
                .get(GET_INGREDIENTS_URL);
        return Optional.ofNullable(response.as(IngredientsResponse.class).getData());

    }

    @Step("Получить доступные ингредиенты в виде: \"тип ингредиента\" - \"список хэшей\"")
    public Map<String, List<String>> getIngredientIdsPerType(){
        var ingredients = getIngredients();
        if (ingredients.isPresent()){
            return ingredients.get().stream().collect(Collectors.groupingBy(
                    Ingredient::getType,
                    HashMap::new,
                    mapping(Ingredient::get_id, toList())));
        } else throw new NoSuchElementException("Не удалось получить список ингредиентов");

    }
}
