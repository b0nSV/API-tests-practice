package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderCreate;
import site.nomoreparties.stellarburgers.buiseness_entities.OrderList;
import site.nomoreparties.stellarburgers.config.BaseApiSpecs;

import static io.restassured.RestAssured.given;

public class OrderSteps extends BaseApiSpecs {

    public static final String ORDER_URL = "/orders";
    public static final String ORDERS_ALL_URL = "/orders/all";

    @Step("Создать заказ")
    public Response createOrder(OrderCreate newOrder, String accessToken) {
        var requestSpecification = given()
                .spec(getReqSpecWithBody(ContentType.JSON)).and()
                .body(newOrder);
        if (accessToken != null) requestSpecification.and().header(createAuthHeader(accessToken));
        return requestSpecification.when().post(ORDER_URL);
    }

    @Step("Получить список всех заказов")
    public OrderList getOrdersAll() {
        return given()
                .spec(getReqSpec())
                .when()
                .get(ORDERS_ALL_URL).as(OrderList.class);
    }

    @Step("Получить список заказов пользователя")
    public Response getOrders(String accessToken) {
        var requestSpecification = given()
                .spec(getReqSpec());
        if (accessToken != null) requestSpecification.and().header(createAuthHeader(accessToken));
        return requestSpecification.when().get(ORDER_URL);
    }
}
