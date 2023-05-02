package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreate;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderList;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    public static final String CREATE_ORDER_URL = "/orders";
    public static final String GET_ORDERS_ALL_URL = "/orders/all";
    public static final String GET_ORDERS_URL = "/orders";

    @Step("Создать заказ авторизованным пользователем")
    public static Response createOrder(OrderCreate newOrder, String accessToken) {
        // null значения из user не добавляются в сформированный json
        return given()
                .spec(BaseApiSpecs.getPostReqSpec())
                .header("Authorization", accessToken)
                .and()
                .body(newOrder)
                .when()
                .post(BaseApiSpecs.BASE_URL + CREATE_ORDER_URL);
    }

    @Step("Создать заказ (без токена)")
    public static Response createOrder(OrderCreate newOrder) {
        // null значения из user не добавляются в сформированный json
        return given()
                .spec(BaseApiSpecs.getPostReqSpec())
                .and()
                .body(newOrder)
                .when()
                .post(BaseApiSpecs.BASE_URL + CREATE_ORDER_URL);
    }

    @Step("Получить список всех заказов")
    public static OrderList getOrdersAll() {
        return given()
                .spec(BaseApiSpecs.getGetReqSpec())
                .when()
                .get(BaseApiSpecs.BASE_URL + GET_ORDERS_ALL_URL).as(OrderList.class);
    }

    @Step("Получить список заказов пользователя")
    public static Response getOrders(String accessToken) {
        return given()
                .spec(BaseApiSpecs.getGetReqSpec())
                .header("Authorization", accessToken)
                .when()
                .get(BaseApiSpecs.BASE_URL + GET_ORDERS_URL);
    }

    @Step("Получить список заказов пользователя (без токена)")
    public static Response getOrders() {
        return given()
                .spec(BaseApiSpecs.getGetReqSpec())
                .when()
                .get(BaseApiSpecs.BASE_URL + GET_ORDERS_URL);
    }

}
