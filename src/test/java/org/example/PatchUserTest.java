package org.example;

import org.example.buiseness_entities.PatchUserResponse;
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

    @BeforeClass
    public static void registerRandomUser() {
        accessToken = registerUser(randomUser).getAuthToken();
    }

    @Test
    public void patchUserWithTokenReturnsStatus200Test(){
        var newUserData = new User("new" + randomUser.getEmail(), null, null);
        Response response = patchUser(newUserData, accessToken);
        assertEquals(SC_OK, response.statusCode());
    }

    @Test
    public void patchUserWithTokenReturnsSuccessTrueTest(){
        var newUserData = new User("new" + randomUser.getEmail(), null, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(PatchUserResponse.class);
        assertTrue(patchUserResponse.isSuccess());
    }

    @Test
    public void patchUserEmailReturnsNewEmailTest(){
        var newEmail = "new" + randomUser.getEmail();
        var newUserData = new User(newEmail, null, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(PatchUserResponse.class);
        assertEquals(newEmail, patchUserResponse.getUser().getEmail());
    }

    @Test
    public void patchUserNameReturnsNewNameTest(){
        var newName = "новый" + randomUser.getName();
        var newUserData = new User(null, null, newName);
        var patchUserResponse = patchUser(newUserData, accessToken).as(PatchUserResponse.class);
        assertEquals(newName, patchUserResponse.getUser().getName());
    }

    @Test
    public void patchUserPasswordDoNotReturnsPasswordTest(){
        var newPassword = createRandomPassword(9);
        var newUserData = new User(null, newPassword, null);
        var patchUserResponse = patchUser(newUserData, accessToken).as(PatchUserResponse.class);
        assertNull(patchUserResponse.getUser().getPassword());
    }

    @AfterClass
    public static void deleteUserAfterTest(){
        if (accessToken != null) deleteUser(accessToken);
    }

}
