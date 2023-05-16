package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.restassured.response.Response;
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
    Response registerResponse;
    Response repeatedRegisterResponse;

    @BeforeEach
    public void registerRandomUser() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        registerResponse = userSteps.registerUser(user);
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationWithAllRequiredParams() {
        assertAll(
                () -> assertEquals(SC_OK, registerResponse.getStatusCode()),
                () -> assertTrue(registerResponse.as(UserLoginResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому"),
                () -> assertNotNull(registerResponse.as(UserLoginResponse.class).getAccessToken(),
                        "В теле ответа не вернулся токен доступа (accessToken)"),
                () -> assertNotNull(registerResponse.as(UserLoginResponse.class).getRefreshToken(),
                        "В теле ответа не вернулся рефреш токен (refreshToken)")
        );
    }

    @Test
    @DisplayName("При успешной регистрации пользователя в теле ответа возвращаеются указанные email, имя")
    public void successfulRegistrationReturnsUserInfoInResponse() {
        assertAll(
                () -> assertEquals(user.getEmail(),
                        registerResponse.as(UserLoginResponse.class).getUser().getEmail(),
                        "В ответе не вернулся email"),
                () -> assertEquals(user.getName()
                        , registerResponse.as(UserLoginResponse.class).getUser().getName(),
                        "В ответе не вернулось имя")
        );
    }

    @DisplayName("Ошибка регистрации без указания обязательных параметров")
    @ParameterizedTest(name = "email={0} | password={1} | name={2}")
    @MethodSource("noRequiredParamsForLoginArguments")
    public void registerWithoutRequiredParamsThrowsError(String email, String password, String name) {
        var user = new User(email, password, name);
        var response = userSteps.registerUser(user);
        assertAll(
                () -> assertEquals(SC_FORBIDDEN, response.getStatusCode()),
                () -> assertEquals("Email, password and name are required fields"
                        , response.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому"),
                () -> assertFalse(response.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому")
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
    @DisplayName("403. Регистрации по уже существующим данным пользователя")
    public void registerUserByExistingUserDataReturnsError() {
        repeatedRegisterResponse = userSteps.registerUser(user);
        assertAll(
                () -> assertEquals(SC_FORBIDDEN, repeatedRegisterResponse.getStatusCode()),
                () -> assertFalse(repeatedRegisterResponse.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому"),
                () -> assertEquals("User already exists"
                        , repeatedRegisterResponse.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому")
        );
    }

    @Test
    @DisplayName("403. Регистрация с использованием существующего email")
    public void registerUserWithExistingEmailReturnsError() {
        repeatedRegisterResponse = userSteps.registerUser(new User(user.getEmail(), createRandomPassword(8), getRandomName()));
        assertAll(
                () -> assertEquals(SC_FORBIDDEN, repeatedRegisterResponse.getStatusCode()),
                () -> assertFalse(repeatedRegisterResponse.as(ErrorMessageResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому"),
                () -> assertEquals("User already exists"
                        , repeatedRegisterResponse.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому")
        );
    }

    @AfterEach
    public void deleteCreatedUser() {
        var registerToken = registerResponse.as(UserLoginResponse.class).getAccessToken();
        //var repeatedRegisterToken = repeatedRegisterResponse.as(UserLoginResponse.class).getAccessToken();
        String repeatedRegisterToken = null;
        if (registerToken != null)
            userSteps.deleteUser(registerToken);
        // Удалить пользователя, если он все таки был создан с использованием данных существующего пользователя
        if (repeatedRegisterResponse != null)
            repeatedRegisterToken = repeatedRegisterResponse.as(UserLoginResponse.class)
                    .getAccessToken();
        if (repeatedRegisterToken != null) userSteps.deleteUser(repeatedRegisterToken);
    }

}
