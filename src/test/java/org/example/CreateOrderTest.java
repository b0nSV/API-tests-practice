package org.example;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.example.buiseness_entities.ErrorMessageResponse;
import org.example.buiseness_entities.OrderCreate;
import org.example.buiseness_entities.OrderCreateResponse;
import org.example.buiseness_entities.User;
import org.junit.*;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;
import static org.example.steps.IngredientSteps.*;
import static org.example.helpers.RandomSequences.*;
import static org.example.helpers.entities.TestsByUrlName.CREATE_ORDER_METHOD_TESTS_NAME;
import static org.example.helpers.entities.IngredientTypes.*;
import static org.example.steps.UserSteps.registerUser;
import static org.example.steps.UserSteps.deleteUser;
import static org.example.steps.OrderSteps.createOrder;


@Feature(CREATE_ORDER_METHOD_TESTS_NAME)
public class CreateOrderTest {

    static String accessToken;
    static Map<String, List<String>> ingredientIdsPerType;
    OrderCreate order;

    @BeforeClass
    public static void getIngredientsList() {
        var user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        accessToken = registerUser(user).getAuthToken();
        ingredientIdsPerType = getIngredientIdsPerType();
    }

    @Test
    @DisplayName("Создание заказа без авторизации возвращает статус код 200")
    public void createOrderWithoutAuthTokenReturnsStatus200Test() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).get(0)),
                (ingredientIdsPerType.get(TYPE_MAIN).get(0)),
                (ingredientIdsPerType.get(TYPE_SAUCE).get(0))
        ));
        var response = createOrder(order);
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
        var response = createOrder(order, accessToken);
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
        var response = createOrder(order, accessToken);
        assertNotEquals(0, response.as(OrderCreateResponse.class).getOrder().getNumber());
    }

    @Test
    @DisplayName("При создание заказа с несуществующим хэшем ингредиента возвращается статус код 500")
    public void createOrderWithWrongIngredientHashReturnsStatus500Test() {
        order = new OrderCreate(List.of("a" + (ingredientIdsPerType.get(TYPE_BUN).get(0).substring(1))));
        var response = createOrder(order, accessToken);
        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает статус код 400")
    public void createOrderWithoutIngredientsReturnsStatus400Test() {
        order = new OrderCreate(List.of());
        var response = createOrder(order, accessToken);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает сообщение об ошибке")
    public void createOrderWithoutIngredientsReturnsErrorMessageTest() {
        order = new OrderCreate(List.of());
        var response = createOrder(order, accessToken);
        assertEquals("Ingredient ids must be provided", response.as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает атрибут \"success\":false")
    public void createOrderWithoutIngredientsReturnsSuccessFalseTest() {
        order = new OrderCreate(List.of());
        var response = createOrder(order, accessToken);
        assertFalse(response.as(ErrorMessageResponse.class).isSuccess());
    }

    @AfterClass
    public static void deleteCourierAfterTests() {
        if (accessToken != null) deleteUser(accessToken);
    }

}
