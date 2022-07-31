package org.example;

import io.qameta.allure.Feature;
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
    public void createOrderWithAuthTokenReturnsStatus200Test() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).get(0)),
                (ingredientIdsPerType.get(TYPE_MAIN).get(0)),
                (ingredientIdsPerType.get(TYPE_SAUCE).get(0))
        ));
        var response = createOrder(order, accessToken);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    public void createOrderWithAuthTokenReturnsOrderNumberTest() {
        order = new OrderCreate(List.of(
                (ingredientIdsPerType.get(TYPE_BUN).get(0)),
                (ingredientIdsPerType.get(TYPE_MAIN).get(0)),
                (ingredientIdsPerType.get(TYPE_SAUCE).get(0))
        ));
        var response = createOrder(order, accessToken);
        assertNotEquals(0, response.as(OrderCreateResponse.class).getOrder().getNumber());
    }

    @Test
    public void createOrderWithWrongIngredientHashReturnsStatus500Test() {
        order = new OrderCreate(List.of("a" + (ingredientIdsPerType.get(TYPE_BUN).get(0).substring(1))));
        var response = createOrder(order, accessToken);
        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void createOrderWithoutIngredientsReturnsStatus400Test() {
        order = new OrderCreate(List.of());
        var response = createOrder(order, accessToken);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createOrderWithoutIngredientsReturnsErrorMessageTest() {
        order = new OrderCreate(List.of());
        var response = createOrder(order, accessToken);
        assertEquals("Ingredient ids must be provided", response.as(ErrorMessageResponse.class).getMessage());
    }

    @Test
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
