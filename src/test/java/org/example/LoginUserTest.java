package org.example;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.example.buiseness_entities.User;
import org.example.buiseness_entities.UserCredentials;
import org.example.buiseness_entities.UserLoginResponse;
import org.example.helpers.entities.ResponseAndToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.example.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;
import static org.example.helpers.RandomSequences.*;
import static org.example.steps.UserSteps.registerUser;
import static org.example.steps.UserSteps.loginUser;
import static org.example.steps.UserSteps.deleteUser;
import static org.junit.Assert.*;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
public class LoginUserTest {

    static ResponseAndToken positiveLoginResponse;
    static User user;

    @BeforeClass
    public static void setupData() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        registerUser(user);
        positiveLoginResponse = loginUser(new UserCredentials(user));
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя статус код ответа 200")
    public void loginUserExistingCredentialsReturnsStatus200Test() {
        assertEquals(SC_OK, positiveLoginResponse.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа атрибут \"success\": true")
    public void loginUserExistingCredentialsReturnsSuccessTrueTest() {
        assertTrue(positiveLoginResponse.getResponse().as(UserLoginResponse.class).isSuccess());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть токен доступа(accessToken)")
    public void loginUserExistingCredentialsReturnsSuccessTokenTest() {
        assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getAccessToken());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть рефреш токен(refreshToken)")
    public void loginUserExistingCredentialsReturnsRefreshTokenTest() {
        assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getRefreshToken());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть имя пользователя")
    public void loginUserExistingCredentialsReturnsUserNameTest() {
        assertEquals(user.getName()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getName());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть email пользователя")
    public void loginUserExistingCredentialsReturnsUserEmailTest() {
        assertEquals(user.getEmail()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @AfterClass
    public static void tearDown() {
        if (positiveLoginResponse.getAuthToken() != null) deleteUser(positiveLoginResponse.getAuthToken());
    }

}
