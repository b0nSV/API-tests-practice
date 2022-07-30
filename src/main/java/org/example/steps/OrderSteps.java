package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.buiseness_entities.OrderCreate;
import org.example.buiseness_entities.OrderList;

import static io.restassured.RestAssured.given;
import static org.example.steps.BaseApiSpecs.*;

public class OrderSteps {

    public static final String CREATE_ORDER_URL = "/orders";
    public static final String GET_ORDERS_ALL_URL = "/orders/all";
    public static final String GET_ORDERS_URL = "/orders";

    @Step("Создать заказ")
    public static Response createOrder(OrderCreate newOrder) {
        // null значения из user не добавляются в сформированный json
        return given()
                .spec(getPostReqSpec())
                .and()
                .body(newOrder)
                .when()
                .post(BASE_URL + CREATE_ORDER_URL);
    }

    @Step("Получить список всех заказов")
    public static OrderList getOrdersAll() {
        return given()
                .spec(getGetReqSpec())
                .when()
                .get(BASE_URL + GET_ORDERS_ALL_URL).as(OrderList.class);
    }

    @Step("Получить список заказов пользователя")
    public static OrderList getOrders(String accessToken) {
        return given()
                .spec(getGetReqSpec())
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URL + GET_ORDERS_URL).as(OrderList.class);
    }

    @Step("Получить список заказов пользователя (без токена)")
    public static OrderList getOrders() {
        return given()
                .spec(getGetReqSpec())
                .when()
                .get(BASE_URL + GET_ORDERS_URL).as(OrderList.class);
    }
}
