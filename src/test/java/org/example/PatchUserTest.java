package org.example;

import io.qameta.allure.junit4.DisplayName;
import org.example.buiseness_entities.ErrorMessageResponse;
import org.example.buiseness_entities.UserPatchResponse;
import org.example.buiseness_entities.User;
import io.qameta.allure.Feature;
import org.junit.*;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.*;
import static org.example.helpers.entities.TestsByUrlName.PARTIAL_UPDATE_USER_METHOD_TESTS_NAME;
import static org.junit.Assert.*;
import static org.example.steps.UserSteps.registerUser;
import static org.example.steps.UserSteps.deleteUser;
import static org.example.steps.UserSteps.patchUser;
import static org.example.helpers.RandomSequences.*;

@Feature(PARTIAL_UPDATE_USER_METHOD_TESTS_NAME)
public class PatchUserTest {

    static User randomUser = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
    static String accessToken;
    String anotherUserAccessToken;

    @BeforeClass
    public static void registerRandomUser() {
        accessToken = registerUser(randomUser).getAuthToken();
    }

    @Test
    @DisplayName("При успешном обновлении данных пользователя возвращается статус код 200")
    public void patchUserWithTokenReturnsStatus200Test() {
        var newUserData = new User("new" + randomUser.getEmail(), null, null);
        Response response = patchUser(newUserData, accessToken);
        assertEquals(SC_OK, response.getStatusCode());
    }

    @Test
    @DisplayName("При успешном обновлении данных пользователя возвращается \"success\": true")
    public void patchUserWithTokenReturnsSuccessTrueTest() {
        var newUserData = new User("new" + randomUser.getEmail(), null, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertTrue(patchUserResponse.isSuccess());
    }

    @Test
    @DisplayName("При успешном обновлении email пользователя в теле ответа возвращается новый email")
    public void patchUserEmailReturnsNewEmailTest() {
        var newEmail = "new" + randomUser.getEmail();
        var newUserData = new User(newEmail, null, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertEquals(newEmail, patchUserResponse.getUser().getEmail());
    }

    @Test
    @DisplayName("При успешном обновлении имени пользователя в теле ответа возвращается новое имя")
    public void patchUserNameReturnsNewNameTest() {
        var newName = "новый" + randomUser.getName();
        var newUserData = new User(null, null, newName);
        var patchUserResponse = patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertEquals(newName, patchUserResponse.getUser().getName());
    }

    @Test
    @DisplayName("При успешном обновлении пароля пользователя в теле ответа не возвращается новый пароль")
    public void patchUserPasswordDoNotReturnsPasswordTest() {
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(UserPatchResponse.class);
        assertNull(patchUserResponse.getUser().getPassword());
    }

    @Test
    @DisplayName("При обновлении данных пользователя без авторизационного токена возвращается статус код 401")
    public void patchUserWithoutTokenReturnsStatus401Test() {
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var patchUserResponse = patchUser(newUserData);
        assertEquals(SC_UNAUTHORIZED, patchUserResponse.getStatusCode());
    }

    @Test
    @DisplayName("При обновлении данных пользователя без авторизационного токена возвращается сообщение об ошибке")
    public void patchUserWithoutTokenReturnsErrorMessageTest() {
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var patchUserResponse = patchUser(newUserData).as(ErrorMessageResponse.class);
        assertEquals("You should be authorised", patchUserResponse.getMessage());
    }

    @Test
    @DisplayName("При попытке обновить email значением принадлежащим другому пользователя статус код ответа 403")
    public void patchUserUsingAnotherUserEmailReturns403Test() {
        var anotherRandomUser = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        anotherUserAccessToken = registerUser(anotherRandomUser).getAuthToken();
        var patchUserResponse = patchUser(new User(anotherRandomUser.getEmail(), null, null), accessToken);
        assertEquals(SC_FORBIDDEN, patchUserResponse.getStatusCode());
    }

    @Test
    @DisplayName("При попытке обновить email значением, принадлежащим другому пользователя, возвращается сообщение об ошибке")
    public void patchUserUsingAnotherUserEmailReturnsErrorMessageTest() {
        var anotherRandomUser = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        anotherUserAccessToken = registerUser(anotherRandomUser).getAuthToken();
        var patchUserResponse = patchUser(new User(anotherRandomUser.getEmail(), null, null), accessToken);
        assertEquals("User with such email already exists", patchUserResponse.as(ErrorMessageResponse.class).getMessage());
    }

    @After
    public void deleteAnotherUserAfterTest() {
        if (anotherUserAccessToken != null) deleteUser(anotherUserAccessToken);
    }

    @AfterClass
    public static void deleteUserAfterTests() {
        if (accessToken != null) deleteUser(accessToken);
    }

}
