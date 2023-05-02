package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserCredentials;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;
import site.nomoreparties.stellarburgers.helpers.entities.ResponseAndToken;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.steps.UserSteps.registerUser;
import static site.nomoreparties.stellarburgers.steps.UserSteps.loginUser;
import static site.nomoreparties.stellarburgers.steps.UserSteps.deleteUser;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
public class LoginUserTest {

    static ResponseAndToken positiveLoginResponse;
    static User user;

    @BeforeAll
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

    @AfterAll
    public static void tearDown() {
        if (positiveLoginResponse.getAuthToken() != null) deleteUser(positiveLoginResponse.getAuthToken());
    }

}
