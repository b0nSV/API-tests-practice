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
    static User user;

    @BeforeAll
    public static void setupData() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        userSteps.registerUser(user);
        positiveLoginResponse = userSteps.loginUser(new UserCredentials(user));
    }

    @Test
    @DisplayName("Успешный логин по существующим кредам пользователя")
    public void successfullloginWithExistingCredentials() {
        assertAll(
                () -> assertEquals(SC_OK, positiveLoginResponse.getResponse().getStatusCode()),
                () -> assertTrue(positiveLoginResponse.getResponse().as(UserLoginResponse.class).isSuccess()),
                () -> assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getAccessToken()),
                () -> assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getRefreshToken())
        );
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть имя пользователя")
    public void loginUserExistingCredentialsReturnsUserName() {
        assertEquals(user.getName()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getName());
    }

    @Test
    @DisplayName("При логине по существующим данным пользователя в теле ответа есть email пользователя")
    public void loginUserExistingCredentialsReturnsUserEmail() {
        assertEquals(user.getEmail()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @ParameterizedTest
    @MethodSource("notAuthorizedLoginArguments")
    public void loginUserWithBadCredentials(String email, String password) {
        var loginResponseAndToken = userSteps.loginUser(new UserCredentials(email, password));
        assertAll(
                () -> assertNull(loginResponseAndToken.getResponse().getHeader("Authorization")),
                () -> assertEquals(SC_UNAUTHORIZED, loginResponseAndToken.getResponse().getStatusCode()),
                () -> assertFalse(loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess()),
                () -> assertEquals("email or password are incorrect",
                        loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage())
        );
    }

    static Stream<Arguments> notAuthorizedLoginArguments() {
        return Stream.of(
                arguments("notExists" + getRandomEmail(), user.getPassword()),
                arguments(user.getEmail(), createRandomPassword(8)),
                arguments("notExists" + getRandomEmail(), createRandomPassword(8)),
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
