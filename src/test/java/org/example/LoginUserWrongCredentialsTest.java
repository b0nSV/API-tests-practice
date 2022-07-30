package org.example;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.example.buiseness_entities.ErrorMessageResponse;
import org.example.buiseness_entities.User;
import org.example.buiseness_entities.UserCredentials;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.example.helpers.RandomSequences.*;
import static org.example.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;
import static org.example.steps.UserSteps.registerUser;
import static org.example.steps.UserSteps.loginUser;
import static org.example.steps.UserSteps.deleteUser;
import static org.junit.Assert.*;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
@Story("Ошибка аутентификации пользователя с неверными данными запроса")
@RunWith(Parameterized.class)
public class LoginUserWrongCredentialsTest {

    private static final User user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
    private final String email;
    private final String password;
    private final int expectedStatusCode;

    public LoginUserWrongCredentialsTest(String email, String password, int expectedStatusCode) {
        this.email = email;
        this.password = password;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters(name = "email = {0} | password = {1} | requiredStatusCode = {2}")
    public static Object[][] getCredentials() {
        return new Object[][]{
                {"notExists" + getRandomEmail(), user.getPassword(), SC_UNAUTHORIZED},
                {user.getEmail(), createRandomPassword(8), SC_UNAUTHORIZED},
                {"notExists" + getRandomEmail(), createRandomPassword(8), SC_UNAUTHORIZED},
                {null, user.getPassword(), SC_UNAUTHORIZED},
                {user.getEmail(), null, SC_UNAUTHORIZED},
                {null, null, SC_UNAUTHORIZED},
        };
    }

    @Test
    public void loginUserWrongCredentialsTest() {
        var registerToken = registerUser(user).getAuthToken();
        var loginResponseAndToken = loginUser(new UserCredentials(email, password));

        assertNull(loginResponseAndToken.getResponse().getHeader("Authorization"));
        assertEquals(expectedStatusCode, loginResponseAndToken.getResponse().getStatusCode());
        assertFalse(loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("email or password are incorrect"
                , loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());

        // Удаление учетной записи в случае успешного логина
        if (registerToken != null) deleteUser(registerToken);
    }
}
