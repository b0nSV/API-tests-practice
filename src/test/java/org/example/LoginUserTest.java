package org.example;

import io.qameta.allure.Feature;
import org.example.buiseness_entities.User;
import org.example.buiseness_entities.UserCredentials;
import org.example.buiseness_entities.UserLoginResponse;
import org.example.helpers.entities.ResponseAndToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.example.helpers.RandomSequences.*;
import static org.example.steps.UserSteps.*;
import static org.junit.Assert.*;

@Feature("Выполнить вход - POST " + LOGIN_COURIER_URL)
public class LoginUserTest {

    static ResponseAndToken positiveLoginResponse;
    static ResponseAndToken wrongEmailLoginResponse;
    static ResponseAndToken wrongPasswordLoginResponse;
    static ResponseAndToken notExistingCredentialsLoginResponse;
    static User user;

    @BeforeClass
    public static void setupData() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        registerUser(user);
        positiveLoginResponse = loginUser(new UserCredentials(user));

        wrongEmailLoginResponse = loginUser(new UserCredentials("notExists" + getRandomEmail(),user.getPassword()));
        wrongPasswordLoginResponse = loginUser(new UserCredentials(user.getEmail(), createRandomPassword(8)));
        notExistingCredentialsLoginResponse = loginUser(new UserCredentials(user.getEmail(), createRandomPassword(8)));
    }

    @Test
    public void loginUserExistingCredentialsReturnsStatus200Test() {
        assertEquals(SC_OK, positiveLoginResponse.getResponse().getStatusCode());
    }

    @Test
    public void loginUserExistingCredentialsReturnsSuccessTrueTest() {
        assertTrue(positiveLoginResponse.getResponse().as(UserLoginResponse.class).isSuccess());
    }

    @Test
    public void loginUserExistingCredentialsReturnsSuccessTokenTest() {
        assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getAccessToken());
    }

    @Test
    public void loginUserExistingCredentialsReturnsRefreshTokenTest() {
        assertNotNull(positiveLoginResponse.getResponse().as(UserLoginResponse.class).getRefreshToken());
    }

    @Test
    public void loginUserExistingCredentialsReturnsUserNameTest() {
        assertEquals(user.getName()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getName());
    }

    @Test
    public void loginUserExistingCredentialsReturnsUserEmailTest() {
        assertEquals(user.getEmail()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @Test
    public void loginUserWrongEmailReturnsUserEmailTest() {
        assertEquals(user.getEmail()
                , positiveLoginResponse.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @AfterClass
    public static void tearDown() {
        if (positiveLoginResponse.getAuthToken() != null) deleteUser(positiveLoginResponse.getAuthToken());
    }
}
