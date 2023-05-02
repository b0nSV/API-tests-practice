package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserCredentials;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.LOGIN_USER_METHOD_TESTS_NAME;
import static site.nomoreparties.stellarburgers.steps.UserSteps.registerUser;
import static site.nomoreparties.stellarburgers.steps.UserSteps.loginUser;
import static site.nomoreparties.stellarburgers.steps.UserSteps.deleteUser;

@Feature(LOGIN_USER_METHOD_TESTS_NAME)
@Story("Ошибка аутентификации пользователя с неверными данными запроса")
@RunWith(Parameterized.class)
public class LoginUserWrongCredentialsTest {

    private static final User user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
    private final String email;
    private final String password;
    private final int expectedStatusCode;
    private String registerToken;

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
        registerToken = registerUser(user).getAuthToken();
        var loginResponseAndToken = loginUser(new UserCredentials(email, password));

        assertNull(loginResponseAndToken.getResponse().getHeader("Authorization"));
        assertEquals(expectedStatusCode, loginResponseAndToken.getResponse().getStatusCode());
        assertFalse(loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("email or password are incorrect"
                , loginResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @After
    public void after() {
        if (registerToken != null) deleteUser(registerToken);
    }

}
