package org.example.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.entities.User;
import org.example.entities.UserCredentials;

import static io.restassured.RestAssured.given;
import static org.example.steps.BaseApiSpecs.*;

public class UserSteps {

    private static final String REGISTER_COURIER_URL = "/auth/register";
    private static final String LOGIN_COURIER_URL = "/auth/login";
    private static final String PATCH_COURIER_URL = "/auth/user";

    @Step("Зарегистрировать пользователя")
    public static Response registerUser(User user) {
        return given()
                .spec(getPostReqSpec())
                .and()
                .body(user)
                .when()
                .post(BASE_URL + REGISTER_COURIER_URL);
    }

    @Step("Обновить данные о пользователе")
    public static Response updateUserInfo(User user, String token) {
        // null значения из user не добавляются в сформированный json
        Gson gson = new Gson();
        return given()
                .spec(getPatchReqSpec())
                .auth().oauth2(token)
                .and()
                .body(gson.toJson(user))
                .when()
                .patch(BASE_URL + PATCH_COURIER_URL);
    }

    @Step("Обновить данные о пользователе без токена")
    public static Response updateUserInfo(User user) {
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
    public static Response deleteUser(String token) {
        return given()
                .spec(getDeleteReqSpec())
                .header("Authorization", token)
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
    public static Response loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getPostReqSpec())
                .and()
                .body(userCredentials)
                .when()
                .post(BASE_URL + LOGIN_COURIER_URL);
    }

}
