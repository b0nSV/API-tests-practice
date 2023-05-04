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
import static org.junit.jupiter.api.Assertions.*;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.IngredientTypes.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.GET_USER_ORDERS_METHOD_TESTS_NAME;

@Feature(GET_USER_ORDERS_METHOD_TESTS_NAME)
public class GetUserOrdersTest extends InitTests {

    static String accessToken;
    static final int countOrder = 3;

    @BeforeAll
    public static void setUp() {
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
    @DisplayName("В списке заказов пользователя возвращаются все созданные заказы")
    public void getUserOrdersReturnsAllCreatedOrders() {
        var getOrdersListResponse = orderSteps.getOrders(accessToken);
        assertEquals(countOrder, getOrdersListResponse.as(OrderList.class).getOrders().size(),
                "Количество созданных заказов в ответе не соответствует ожидаемому");
    }

    @Test
    @DisplayName("При получении списка заказов с авторизационным токеном возвращается статус код 200")
    public void getUserOrdersWithAuthTokenReturnsStatus200() {
        var getOrdersListResponse = orderSteps.getOrders(accessToken);
        assertEquals(SC_OK, getOrdersListResponse.getStatusCode());
    }

    @Test
    @DisplayName("401. Запрос списка заказов пользователя без авторизационного токена")
    public void getUserOrdersWithoutAuthTokenReturnsError() {
        var getOrdersListResponse = orderSteps.getOrders(null);
        assertAll(
                () -> assertEquals(SC_UNAUTHORIZED, getOrdersListResponse.getStatusCode()),
                () -> assertEquals("You should be authorised",
                        getOrdersListResponse.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому"),
                () -> assertFalse(getOrdersListResponse.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому")
        );
    }

    @AfterAll
    public static void deleteCourierAfterTests() {
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

}
