package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserCredentials;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;

import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
public class LoginUserTest extends InitTests {

    static Response successfullLoginResponse;
    static final User user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());

    @BeforeAll
    public static void setupData() {
        userSteps.registerUser(user);
        successfullLoginResponse = userSteps.loginUser(new UserCredentials(user));
    }

    @Test
    @DisplayName("Успешный логин по существующим кредам пользователя")
    public void successfulLoginWithExistingCredentials() {
        assertAll(
                () -> assertEquals(SC_OK, successfullLoginResponse.getStatusCode()),
                () -> assertTrue(successfullLoginResponse.as(UserLoginResponse.class).isSuccess()),
                () -> assertNotNull(successfullLoginResponse.as(UserLoginResponse.class).getAccessToken()),
                () -> assertNotNull(successfullLoginResponse.as(UserLoginResponse.class).getRefreshToken())
        );
    }

    @Test
    @DisplayName("При успешном логине в теле ответа также возвращаются данные пользователя")
    public void successfulLoginReturnsUserInfoInResponse() {
        assertAll(
                () -> assertEquals(user.getName()
                        , successfullLoginResponse.as(UserLoginResponse.class).getUser().getName(),
                        "Имя пользователя в ответе не соответствует ожидаемому"),
                () -> assertEquals(user.getEmail()
                        , successfullLoginResponse.as(UserLoginResponse.class).getUser().getEmail(),
                        "email в ответе не соответствует ожидаемому")
        );

    }

    @DisplayName("401. Вход по несуществующей связке email + pass.")
    @ParameterizedTest(name = "\"email\":{0} \"password\":{1}")
    @MethodSource("notAuthorizedLoginArguments")
    public void loginUserWithBadCredentials(String email, String password) {
        var loginResponse = userSteps.loginUser(new UserCredentials(email, password));
        assertAll(
                () -> assertNull(loginResponse.getHeader("Authorization"),
                        "В ответе вернулся авторизационный токен"),
                () -> assertEquals(SC_UNAUTHORIZED, loginResponse.getStatusCode()),
                () -> assertFalse(loginResponse.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому"),
                () -> assertEquals("email or password are incorrect",
                        loginResponse.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому")
        );
    }

    static Stream<Arguments> notAuthorizedLoginArguments() {
        return Stream.of(
                arguments("not_exists" + getRandomEmail(), user.getPassword()),
                arguments(user.getEmail(), createRandomPassword(8)),
                arguments("not_exists" + getRandomEmail(), createRandomPassword(8)),
                arguments(null, user.getPassword()),
                arguments(user.getEmail(), null),
                arguments(null, null)
        );
    }

    @AfterAll
    public static void tearDown() {
        var accessToken = successfullLoginResponse.as(UserLoginResponse.class).getAccessToken();
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

}
