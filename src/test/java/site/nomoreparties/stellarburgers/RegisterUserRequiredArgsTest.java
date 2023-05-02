package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.helpers.entities.ResponseAndToken;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.REGISTER_USER_METHOD_TESTS_NAME;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.steps.UserSteps.registerUser;
import static site.nomoreparties.stellarburgers.steps.UserSteps.deleteUser;

@Feature(REGISTER_USER_METHOD_TESTS_NAME)
@Story("Ошибка регистрации пользователя без обязательных параметров запроса")
@RunWith(Parameterized.class)
public class RegisterUserRequiredArgsTest {

    private final String email;
    private final String password;
    private final String name;
    private final int statusCode;
    private ResponseAndToken responseAndToken;

    public RegisterUserRequiredArgsTest(String email, String password, String name, int statusCode) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.statusCode = statusCode;
    }

    @Parameterized.Parameters(name = "email = {0} | password = {1} | name = {2} | requiredStatusCode = {3}")
    public static Object[][] getUserData() {
        return new Object[][]{
                {getRandomEmail(), createRandomPassword(8), null, SC_FORBIDDEN},
                {getRandomEmail(), null, getRandomName(), SC_FORBIDDEN},
                {null, createRandomPassword(8), getRandomName(), SC_FORBIDDEN},
                {getRandomEmail(), null, null, SC_FORBIDDEN},
                {null, null, getRandomName(), SC_FORBIDDEN},
                {null, createRandomPassword(8), null, SC_FORBIDDEN},
                {null, null, null, SC_FORBIDDEN},
        };
    }

    @Test
    public void registerUserRequiredArgs() {
        var user = new User(email, password, name);
        responseAndToken = registerUser(user);

        assertEquals(statusCode, responseAndToken.getResponse().getStatusCode());
        assertEquals("Email, password and name are required fields"
                , responseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
        assertFalse(responseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
    }

    @After
    public void after() {
        // Удаление пользователя, если он все таки был создан
        if (responseAndToken.getAuthToken() != null) deleteUser(responseAndToken.getAuthToken());
    }

}
