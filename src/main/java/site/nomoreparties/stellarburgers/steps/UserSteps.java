package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserCredentials;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;
import site.nomoreparties.stellarburgers.config.BaseApiSpecs;
import site.nomoreparties.stellarburgers.helpers.entities.ResponseAndToken;

import static io.restassured.RestAssured.given;

public class UserSteps extends BaseApiSpecs {

    public static final String REGISTER_COURIER_URL = "/auth/register";
    public static final String LOGIN_COURIER_URL = "/auth/login";
    public static final String COURIER_URL = "/auth/user";

    @Step("Зарегистрировать пользователя")
    public ResponseAndToken registerUser(User user) {
        var response = given()
                .spec(getReqSpecWithBody(ContentType.JSON)).and()
                .body(user)
                .when()
                .post(REGISTER_COURIER_URL);
        return new ResponseAndToken(response, response.as(UserLoginResponse.class).getAccessToken());
    }

    @Step("Обновить данные о пользователе")
    public Response patchUser(User user, String accessToken) {
        var requestSpecification = given()
                .spec(getReqSpecWithBody(ContentType.JSON)).and()
                .body(user);
        if (accessToken != null) requestSpecification.and().header(createAuthHeader(accessToken));
        return requestSpecification.when().patch(COURIER_URL);
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        var requestSpecification = given().spec(getReqSpec());
        if (accessToken != null) requestSpecification.and().header(createAuthHeader(accessToken));
        return requestSpecification.when().delete(COURIER_URL);
    }

    @Step("Выполнить вход")
    public ResponseAndToken loginUser(UserCredentials userCredentials) {
        var response = given()
                .spec(getReqSpecWithBody(ContentType.JSON)).and()
                .body(userCredentials)
                .when()
                .post(LOGIN_COURIER_URL);
        return new ResponseAndToken(response, response.as(UserLoginResponse.class).getAccessToken());
    }
}
