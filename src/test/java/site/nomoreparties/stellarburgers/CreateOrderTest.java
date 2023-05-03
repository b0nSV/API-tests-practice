package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreate;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreateResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.IngredientTypes.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.CREATE_ORDER_METHOD_TESTS_NAME;


@Feature(CREATE_ORDER_METHOD_TESTS_NAME)
public class CreateOrderTest extends InitTests {

    static String accessToken;
    static Map<String, List<String>> ingredientIdsPerType;
    OrderCreate order;

    @BeforeAll
    public static void getIngredientsList() {
        var user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        accessToken = userSteps.registerUser(user).getAuthToken();
        ingredientIdsPerType = ingredientSteps.getIngredientIdsPerType();
    }

    @Test
    @DisplayName("Создание заказа без авторизации возвращает статус код 200")
    public void createOrderWithoutAuthTokenReturnsStatus200Test() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).get(0)),
                (ingredientIdsPerType.get(TYPE_MAIN).get(0)),
                (ingredientIdsPerType.get(TYPE_SAUCE).get(0))
        ));
        var response = orderSteps.createOrder(order, null);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа с авторизационным токеном возвращает статус код 200")
    public void createOrderWithAuthTokenReturnsStatus200Test() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).stream().findAny().orElse("")),
                (ingredientIdsPerType.get(TYPE_MAIN).stream().findAny().orElse("")),
                (ingredientIdsPerType.get(TYPE_SAUCE).stream().findAny().orElse(""))
        ));
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @DisplayName("После успешного создания заказа возвращается номер заказа")
    public void createOrderWithAuthTokenReturnsOrderNumberTest() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).stream().findAny().orElse("")),
                (ingredientIdsPerType.get(TYPE_MAIN).stream().findAny().orElse("")),
                (ingredientIdsPerType.get(TYPE_SAUCE).stream().findAny().orElse(""))
        ));
        var response = orderSteps.createOrder(order, accessToken);
        assertNotEquals(0, response.as(OrderCreateResponse.class).getOrder().getNumber());
    }

    @Test
    @DisplayName("При создание заказа с несуществующим хэшем ингредиента возвращается статус код 500")
    public void createOrderWithWrongIngredientHashReturnsStatus500Test() {
        order = new OrderCreate(List.of("a" + (ingredientIdsPerType.get(TYPE_BUN).get(0).substring(1))));
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает статус код 400")
    public void createOrderWithoutIngredientsReturnsStatus400Test() {
        order = new OrderCreate(List.of());
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает сообщение об ошибке")
    public void createOrderWithoutIngredientsReturnsErrorMessageTest() {
        order = new OrderCreate(List.of());
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals("Ingredient ids must be provided", response.as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает атрибут \"success\":false")
    public void createOrderWithoutIngredientsReturnsSuccessFalseTest() {
        order = new OrderCreate(List.of());
        var response = orderSteps.createOrder(order, accessToken);
        assertFalse(response.as(ErrorMessageResponse.class).isSuccess());
    }

    @AfterAll
    public static void deleteCourierAfterTests() {
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

}
