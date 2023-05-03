package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreate;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderList;
import site.nomoreparties.stellarburgers.buiseness_entities.User;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.IngredientTypes.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.GET_USER_ORDERS_METHOD_TESTS_NAME;

@Feature(GET_USER_ORDERS_METHOD_TESTS_NAME)
public class GetUserOrdersTest extends InitTests {

    static String accessToken;
    static final int countOrder = 3;

    @BeforeAll
    public static void getIngredientsList() {
        var user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        accessToken = userSteps.registerUser(user).getAuthToken();
        var ingredientIdsPerType = ingredientSteps.getIngredientIdsPerType();
        var order = new OrderCreate(List.of(
                ingredientIdsPerType.get(TYPE_BUN).stream().findAny().orElse(""),
                ingredientIdsPerType.get(TYPE_MAIN).stream().findAny().orElse(""),
                ingredientIdsPerType.get(TYPE_SAUCE).stream().findAny().orElse("")
        ));

        for (int i = 0; i < countOrder; i++) {
            orderSteps.createOrder(order, accessToken);
        }
    }

    @Test
    @DisplayName("В теле ответа количество заказов соответствует количество созданных пользователем заказов")
    public void getUserOrdersWithAuthTokenReturnsOrdersTest() {
        var getOrdersListResponse = orderSteps.getOrders(accessToken);
        assertEquals(countOrder, getOrdersListResponse.as(OrderList.class).getOrders().size());
    }

    @Test
    @DisplayName("При получении списка заказов с авторизационным токеном возвращается статус код 200")
    public void getUserOrdersWithAuthTokenReturnsStatus200Test() {
        var getOrdersListResponse = orderSteps.getOrders(accessToken);
        assertEquals(SC_OK, getOrdersListResponse.getStatusCode());
    }

    @Test
    @DisplayName("При получении списка заказов без авторизационного токена возвращается статус код 401")
    public void getUserOrdersWithoutAuthTokenReturnsStatus401Test() {
        var getOrdersListResponse = orderSteps.getOrders(null);
        assertEquals(SC_UNAUTHORIZED, getOrdersListResponse.getStatusCode());
    }

    @Test
    @DisplayName("При получении списка заказов без авторизационного токена возвращается сообщение об ошибке")
    public void getUserOrdersWithoutAuthTokenReturnsErrorMessageTest() {
        var getOrdersListResponse = orderSteps.getOrders(null);
        assertEquals("You should be authorised",
                getOrdersListResponse.as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    @DisplayName("При получении списка заказов без авторизационного токена возвращается атрибут \"success\": false")
    public void getUserOrdersWithoutAuthTokenReturnsSuccessFalseTest() {
        var getOrdersListResponse = orderSteps.getOrders(null);
        assertFalse(getOrdersListResponse.as(ErrorMessageResponse.class).isSuccess());
    }

    @AfterAll
    public static void deleteCourierAfterTests() {
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

}
