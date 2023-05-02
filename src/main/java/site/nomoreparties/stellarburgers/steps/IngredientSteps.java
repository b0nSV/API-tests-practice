package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import site.nomoreparties.stellarburgers.buiseness_entities.Ingredient;
import site.nomoreparties.stellarburgers.buiseness_entities.IngredientsResponse;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_OK;
import static site.nomoreparties.stellarburgers.steps.BaseApiSpecs.BASE_URL;
import static site.nomoreparties.stellarburgers.steps.BaseApiSpecs.getGetReqSpec;

public class IngredientSteps {

    public static final String GET_INGREDIENTS_URL = "/ingredients";

    @Step("Получить доступные ингредиенты")
    public static List<Ingredient> getIngredients() {
        var response = given()
                .spec(getGetReqSpec())
                .when()
                .get(BASE_URL + GET_INGREDIENTS_URL);
        if (response.getStatusCode() == SC_OK)
            return response.as(IngredientsResponse.class).getData();
        else {
            Assert.fail("Не удалось получить список доступных ингредиентов");
            return null;
        }
    }

/*    @Step("Создать словарь \"тип ингредиента\" - \"список хэшей\"")
    public static HashMap<String, ArrayList<String>> getIngredientsHashesByType(List<Ingredient> ingredients) {
        var ingredientsHashesByType = new HashMap<String, ArrayList<String>>();
        for (String type : new String[]{TYPE_BUN, TYPE_SAUCE, TYPE_MAIN}) {
            var filteredIngredients = ingredients.stream().filter(ingredient
                    -> ingredient.getType().equals(type)).collect(toList());
            var ingredientsHashes = new ArrayList<String>();
            for (Ingredient ingredient : filteredIngredients) {
                ingredientsHashes.add(Ingredient.getId(ingredient));
            }
            ingredientsHashesByType.put(type, ingredientsHashes);
        }
        return ingredientsHashesByType;
    }*/

    @Step("Создать словарь \"тип ингредиента\" - \"список хэшей\"")
    public static Map<String, List<String>> getIngredientIdsPerType() {
        var ingredients = getIngredients();
        assert ingredients != null;
        return ingredients.stream().collect(Collectors.groupingBy(
                Ingredient::getType,
                HashMap::new,
                mapping(Ingredient::get_id, toList())));
    }
}
