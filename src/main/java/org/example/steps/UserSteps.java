package org.example.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.buiseness_entities.User;
import org.example.buiseness_entities.UserCredentials;
import org.example.buiseness_entities.UserLoginResponse;
import org.example.helpers.entities.ResponseAndToken;

import static io.restassured.RestAssured.given;
import static org.example.steps.BaseApiSpecs.*;

public class UserSteps {

    public static final String REGISTER_COURIER_URL = "/auth/register";
    public static final String LOGIN_COURIER_URL = "/auth/login";
    public static final String PATCH_COURIER_URL = "/auth/user";

    @Step("Зарегистрировать пользователя")
    public static ResponseAndToken registerUser(User user) {
        var response = given()
                .spec(getPostReqSpec())
                .and()
                .body(user)
                .when()
                .post(BASE_URL + REGISTER_COURIER_URL);
        return new ResponseAndToken(response, response.as(UserLoginResponse.class).getAccessToken());
    }

    @Step("Обновить данные о пользователе")
    public static Response patchUser(User user, String accessToken) {
        // null значения из user не добавляются в сформированный json
        Gson gson = new Gson();
        return given()
                .spec(getPatchReqSpec())
                .header("Authorization", accessToken)
                .and()
                .body(gson.toJson(user))
                .when()
                .patch(BASE_URL + PATCH_COURIER_URL);
    }

    @Step("Обновить данные о пользователе без токена")
    public static Response patchUser(User user) {
        // null значения из user не добавляются в сформированный json
        Gson gson = new Gson();
        return given()
                .spec(getPatchReqSpec())
                .and()
                .body(gson.toJson(user))
                .when()
                .patch(BASE_URL + PATCH_COURIER_URL);
    }

    @Step("Удалить пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .spec(getDeleteReqSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + PATCH_COURIER_URL);
    }

    @Step("Удалить пользователя без токена")
    public static Response deleteUser() {
        return given()
                .spec(getDeleteReqSpec())
                .when()
                .delete(BASE_URL + PATCH_COURIER_URL);
    }

    @Step("Выполнить вход пользователем в систему")
    public static ResponseAndToken loginUser(UserCredentials userCredentials) {
        var response = given()
                .spec(getPostReqSpec())
                .and()
                .body(userCredentials)
                .when()
                .post(BASE_URL + LOGIN_COURIER_URL);
        return new ResponseAndToken(response, response.as(UserLoginResponse.class).getAccessToken());
    }

}
