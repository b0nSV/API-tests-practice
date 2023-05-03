package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;
import site.nomoreparties.stellarburgers.helpers.entities.ResponseAndToken;

import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.REGISTER_USER_METHOD_TESTS_NAME;

@Feature(REGISTER_USER_METHOD_TESTS_NAME)
public class RegisterUserTest extends InitTests {

    User user;
    ResponseAndToken registerResponseAndToken;
    ResponseAndToken repeatedRegisterResponseAndToken;

    @BeforeEach
    public void beforeRegisterRandomUser() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        registerResponseAndToken = userSteps.registerUser(user);
    }

    @Test
    @DisplayName("Регистрация пользователя с заполнением всех обязательных полей возвращает статус код 200")
    public void registerUserWithRequiredArgsReturnsStatus200Test() {
        assertEquals(SC_OK, registerResponseAndToken.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращается \"success\":true")
    public void registerUserWithRequiredArgsReturnsSuccessTrueTest() {
        assertTrue(registerResponseAndToken.getResponse().as(UserLoginResponse.class).isSuccess());
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращается токен доступа(accessToken)")
    public void registerUserWithRequiredArgsReturnsAccessTokenTest() {
        assertNotNull(registerResponseAndToken.getResponse().as(UserLoginResponse.class).getAccessToken());
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращается рефреш токен(refreshToken)")
    public void registerUserWithRequiredArgsReturnsRefreshTokenTest() {
        assertNotNull(registerResponseAndToken.getResponse().as(UserLoginResponse.class).getRefreshToken());
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращается указанный email")
    public void registerUserWithRequiredArgsReturnsUserEmailTest() {
        assertEquals(user.getEmail()
                , registerResponseAndToken.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращается указанное имя")
    public void registerUserWithRequiredArgsReturnsUserNameTest() {
        assertEquals(user.getName()
                , registerResponseAndToken.getResponse().as(UserLoginResponse.class).getUser().getName());
    }

    @ParameterizedTest
    @MethodSource("noRequiredParamsForLoginArguments")
    public void registerWithoutRequiredParamsThrowsError(String email, String password, String name) {
        var user = new User(email, password, name);
        var responseAndToken = userSteps.registerUser(user);
        assertAll(
                () -> assertEquals(SC_FORBIDDEN, responseAndToken.getResponse().getStatusCode()),
                () -> assertEquals("Email, password and name are required fields"
                        , responseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage()),
                () -> assertFalse(responseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess())
        );
    }

    static Stream<Arguments> noRequiredParamsForLoginArguments() {
        return Stream.of(
                arguments(getRandomEmail(), createRandomPassword(8), null),
                arguments(getRandomEmail(), null, getRandomName()),
                arguments(null, createRandomPassword(8), getRandomName()),
                arguments(getRandomEmail(), null, null),
                arguments(null, null, getRandomName()),
                arguments(null, createRandomPassword(8), null),
                arguments(null, null, null)
        );
    }

    @Test
    @DisplayName("При попытке регистрации по уже существующим данным пользователя статус код ответа 403")
    public void registerUserByExistingUserDataReturns403Test() {
        repeatedRegisterResponseAndToken = userSteps.registerUser(user);
        assertEquals(SC_FORBIDDEN, repeatedRegisterResponseAndToken.getResponse().getStatusCode());
        assertFalse(repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("User already exists"
                , repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    @DisplayName("При попытке регистрации с уже существующим значением email статус код ответа 403")
    public void registerUserWithExistingUserEmailReturns403Test() {
        repeatedRegisterResponseAndToken = userSteps.registerUser(new User(user.getEmail(), createRandomPassword(8), getRandomName()));
        assertEquals(SC_FORBIDDEN, repeatedRegisterResponseAndToken.getResponse().getStatusCode());
        assertFalse(repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("User already exists"
                , repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @AfterEach
    public void afterDeleteUser() {
        if (registerResponseAndToken.getAuthToken() != null)
            userSteps.deleteUser(registerResponseAndToken.getAuthToken());
        // Удалить пользователя, если он был создан с использованием данных существующего пользователя
        if (repeatedRegisterResponseAndToken != null) {
            if (repeatedRegisterResponseAndToken.getAuthToken() != null)
                userSteps.deleteUser(registerResponseAndToken.getAuthToken());
        }
    }

}
