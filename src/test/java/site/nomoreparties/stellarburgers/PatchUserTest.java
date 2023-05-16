package site.nomoreparties.stellarburgers;


import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.opentest4j.TestAbortedException;
import site.nomoreparties.stellarburgers.buiseness_entities.ErrorMessageResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.User;
import site.nomoreparties.stellarburgers.buiseness_entities.UserLoginResponse;
import site.nomoreparties.stellarburgers.buiseness_entities.UserPatchResponse;

import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static site.nomoreparties.stellarburgers.helpers.RandomSequences.*;
import static site.nomoreparties.stellarburgers.helpers.entities.TestsByUrlName.PARTIAL_UPDATE_USER_METHOD_TESTS_NAME;

@Feature(PARTIAL_UPDATE_USER_METHOD_TESTS_NAME)
public class PatchUserTest extends InitTests {

    static final User randomUser = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
    static String accessToken;
    String anotherUserAccessToken;

    @BeforeAll
    public static void registerRandomUser() {
        accessToken = registerUserAndGetAccessToken(randomUser)
                .orElseThrow(() -> new TestAbortedException("Возникла ошибка при получении токена доступа"));
    }

    @Test
    @DisplayName("При успешном обновлении данных пользователя возвращается статус код 200")
    public void successfulPatchUserReturnsStatus200() {
        var newUserData = new User("new" + randomUser.getEmail(), null, null);
        Response response = userSteps.patchUser(newUserData, accessToken);
        assertAll(
                () -> assertEquals(SC_OK, response.getStatusCode()),
                () -> assertTrue(response.as(UserPatchResponse.class).isSuccess(),
                        "Значение параметра \"success\" не соответствует ожидаемому")
        );
    }

    @Test
    @DisplayName("При успешном обновлении email пользователя в теле ответа возвращается новый email")
    public void patchUserEmailReturnsNewEmail() {
        var newEmail = "new" + randomUser.getEmail();
        var newUserData = new User(newEmail, null, null);
        var patchUserResponse = userSteps.patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertEquals(newEmail, patchUserResponse.getUser().getEmail(),
                "Вернулся не новый email");
    }

    @Test
    @DisplayName("При успешном обновлении имени пользователя в теле ответа возвращается новое имя")
    public void patchUserNameReturnsNewNameTest() {
        var newName = "новый" + randomUser.getName();
        var newUserData = new User(null, null, newName);
        var patchUserResponse = userSteps.patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertEquals(newName, patchUserResponse.getUser().getName(),
                "Вернулось не новое имя");
    }

    @Test
    @DisplayName("При успешном обновлении пароля пользователя в теле ответа не возвращается новый пароль")
    public void newPasswordDoesNotReturnInResponse() {
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var patchUserResponse = userSteps.patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertNull(patchUserResponse.getUser().getPassword(), "В ответе вернулся новый пароль");
    }

    @Test
    @DisplayName("401. Запрос обновления данных пользователя без авторизационного токена")
    public void patchUserWithoutTokenReturnsError() {
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var response = userSteps.patchUser(newUserData, null);
        assertAll(
                () -> assertEquals(SC_UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals("You should be authorised", response.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому")
        );
    }

    @Test
    @DisplayName("403. Обновление email значением, принадлежащим другому пользователю, запрещено")
    public void pathEmailWithOwnedByAnotherUserValueNotAvailable() {
        var anotherRandomUser = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        anotherUserAccessToken = userSteps.registerUser(anotherRandomUser).as(UserLoginResponse.class).getAccessToken();
        var response = userSteps.patchUser(new User(anotherRandomUser.getEmail(), null, null), accessToken);
        assertAll(
                () -> assertEquals(SC_FORBIDDEN, response.getStatusCode()),
                () -> assertEquals("User with such email already exists",
                        response.as(ErrorMessageResponse.class).getMessage(),
                        "Текст ошибки не соответствует ожидаемому")
        );

    }

    @AfterEach
    public void deleteAnotherUserAfterTest() {
        if (anotherUserAccessToken != null) userSteps.deleteUser(anotherUserAccessToken);
    }

    @AfterAll
    public static void deleteUserAfterTests() {
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

}
