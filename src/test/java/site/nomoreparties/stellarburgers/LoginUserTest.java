package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
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
import site.nomoreparties.stellarburgers.helpers.entities.ResponseAndToken;

import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
public class LoginUserTest extends InitTests {

    static ResponseAndToken positiveLoginResponse;
    static final User user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());

    @BeforeAll
    public static void setupData() {
        userSteps.registerUser(user);
        positiveLoginResponse = userSteps.loginUser(new UserCredentials(user));
    }

    @Test
    @DisplayName("Успешный логин по существующим кредам пользователя")
    public void successfulLoginWithExistingCredentials() {
        assertAll(
                () -> assertEquals(SC_OK, positiveLoginResponse.getResponse().getStatusCode()),
                () -> assertTrue(positiveLoginResponse.getResponse().as(UserLoginResponse.class).isSuccess()),
                () -> assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getAccessToken()),
                () -> assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getRefreshToken())
        );
    }

    @Test
    @DisplayName("При успешном логине в теле ответа также возвращаются данные пользователя")
    public void successfulLoginReturnsUserInfoInResponse() {
        assertAll(
                () -> assertEquals(user.getName()
                        , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getName(),
                        "Имя пользователя в ответе не соответствует ожидаемому"),
                () -> assertEquals(user.getEmail()
                        , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getEmail(),
                        "email в ответе не соответствует ожидаемому")
        );

    }

    @DisplayName("401. Вход по несуществующей связке email + pass.")
    @ParameterizedTest(name = "\"email\":{0} \"password\":{1}")
    @MethodSource("notAuthorizedLoginArguments")
    public void loginUserWithBadCredentials(String email, String password) {
        var loginResponseAndToken = userSteps.loginUser(new UserCredentials(email, password));
        assertAll(
                () -> assertNull(loginResponseAndToken.getResponse().getHeader("Authorization"),
                        "В ответе вернулся авторизационный токен"),
                () -> assertEquals(SC_UNAUTHORIZED, loginResponseAndToken.getResponse().getStatusCode()),
                () -> assertFalse(loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому"),
                () -> assertEquals("email or password are incorrect",
                        loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage(),
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
        if (positiveLoginResponse.getAuthToken() != null) userSteps.deleteUser(positiveLoginResponse.getAuthToken());
    }

}
