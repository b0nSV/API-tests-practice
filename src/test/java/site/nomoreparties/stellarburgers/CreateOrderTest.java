package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreate;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreateResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static site.nomoreparties.stellarburgers.buiseness_entities.IngredientTypes.*;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.CREATE_ORDER_METHOD_TESTS_NAME;

@Feature(CREATE_ORDER_METHOD_TESTS_NAME)
public class CreateOrderTest extends InitTests {

    static String accessToken;
    static Map<String, List<String>> ingredientIdsPerType;
    OrderCreate order;

    @BeforeAll
    public static void setUp() {
        var user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        accessToken = registerUserAndGetAccessToken(user)
                .orElseThrow(() -> new TestAbortedException("Возникла ошибка при получении токена доступа"));
        ingredientIdsPerType = ingredientSteps.getIngredientIdsPerType();
    }

    @Test
    @DisplayName("Можно создать заказ без авторизации")
    public void canCreateOrderWithoutAuthentication() {
        order = orderWithEachTypeOfIngredient();
        var response = orderSteps.createOrder(order, null);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Можно создать заказ авторизованным пользователем")
    public void canCreateOrderWithWhenAuthorized() {
        order = orderWithEachTypeOfIngredient();
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @DisplayName("После успешного создания заказа возвращается номер заказа")
    public void createNewOrderReturnsOrderNumber() {
        order = orderWithEachTypeOfIngredient();
        var response = orderSteps.createOrder(order, accessToken);
        assertNotEquals(0, response.as(OrderCreateResponse.class).getOrder().getNumber(),
                "Заказ не был создан");
    }

    @Test
    @DisplayName("При создание заказа с несуществующим хэшем ингредиента возвращается ошибка")
    public void createOrderWithWrongIngredientHashReturnsError() {
        order = new OrderCreate(List.of("a" + ingredientIdsPerType.get(TYPE_BUN.getName()).stream().findAny()
                .orElseThrow(() -> new TestAbortedException("В доступных ингредиентах нет булок")).substring(1)));
        var response = orderSteps.createOrder(order, accessToken);
        assertEquals(SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает ошибку")
    public void createOrderWithoutIngredientsReturnsError() {
        order = new OrderCreate(List.of());
        var response = orderSteps.createOrder(order, accessToken);
        assertAll(
                () -> assertEquals(SC_BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals("Ingredient ids must be provided",
                        response.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому"),
                () -> assertFalse(response.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому")
        );
    }

    @AfterAll
    public static void deleteCourierAfterTests() {
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

    private OrderCreate orderWithEachTypeOfIngredient() {
        return new OrderCreate(List.of(
                ingredientIdsPerType.get(TYPE_BUN.getName()).stream().findAny()
                        .orElseThrow(() -> new TestAbortedException("В доступных ингредиентах нет булок")),
                ingredientIdsPerType.get(TYPE_MAIN.getName()).stream().findAny()
                        .orElseThrow(() -> new TestAbortedException("В доступных ингредиентах нет начинок")),
                ingredientIdsPerType.get(TYPE_SAUCE.getName()).stream().findAny()
                        .orElseThrow(() -> new TestAbortedException("В доступных ингредиентах нет соусов"))
        ));
    }

}
