package org.example;

import io.qameta.allure.Feature;
import org.example.buiseness_entities.ErrorMessageResponse;
import org.example.buiseness_entities.OrderCreate;
import org.example.buiseness_entities.OrderList;
import org.example.buiseness_entities.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.example.helpers.RandomSequences.*;
import static org.example.helpers.entities.TestsByUrlName.GET_USER_ORDERS_METHOD_TESTS_NAME;
import static org.example.helpers.entities.IngredientTypes.*;
import static org.example.steps.IngredientSteps.getIngredientIdsPerType;
import static org.example.steps.OrderSteps.createOrder;
import static org.example.steps.OrderSteps.getOrders;
import static org.example.steps.UserSteps.deleteUser;
import static org.example.steps.UserSteps.registerUser;
import static org.junit.Assert.*;

@Feature(GET_USER_ORDERS_METHOD_TESTS_NAME)
public class GetUserOrdersTest {
    static String accessToken;
    static final int countOrder = 3;

    @BeforeClass
    public static void getIngredientsList() {
        var user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        accessToken = registerUser(user).getAuthToken();
        var ingredientIdsPerType = getIngredientIdsPerType();
        var order = new OrderCreate(List.of(
                ingredientIdsPerType.get(TYPE_BUN).stream().findAny().orElse(""),
                ingredientIdsPerType.get(TYPE_MAIN).stream().findAny().orElse(""),
                ingredientIdsPerType.get(TYPE_SAUCE).stream().findAny().orElse("")
        ));

        for (int i = 0; i < countOrder; i++) {
            createOrder(order, accessToken);
        }
    }

    @Test
    public void getUserOrdersWithAuthTokenReturnsOrdersTest() {
        var getOrdersListResponse = getOrders(accessToken);
        assertEquals(countOrder, getOrdersListResponse.as(OrderList.class).getOrders().size());
    }

    @Test
    public void getUserOrdersWithAuthTokenReturnsStatus200Test() {
        var getOrdersListResponse = getOrders(accessToken);
        assertEquals(SC_OK, getOrdersListResponse.getStatusCode());
    }

    @Test
    public void getUserOrdersWithoutAuthTokenReturnsStatus401Test() {
        var getOrdersListResponse = getOrders();
        assertEquals(SC_UNAUTHORIZED, getOrdersListResponse.getStatusCode());
    }

    @Test
    public void getUserOrdersWithoutAuthTokenReturnsErrorMessageTest() {
        var getOrdersListResponse = getOrders();
        assertEquals("You should be authorised",
                getOrdersListResponse.as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    public void getUserOrdersWithoutAuthTokenReturnsSuccessFalseTest() {
        var getOrdersListResponse = getOrders();
        assertFalse(getOrdersListResponse.as(ErrorMessageResponse.class).isSuccess());
    }

    @AfterClass
    public static void deleteCourierAfterTests() {
        if (accessToken != null) deleteUser(accessToken);
    }
}
