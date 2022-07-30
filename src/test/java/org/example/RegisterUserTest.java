package org.example;

import io.qameta.allure.Feature;
import org.example.buiseness_entities.ErrorMessageResponse;
import org.example.buiseness_entities.User;
import org.example.buiseness_entities.UserLoginResponse;
import org.example.helpers.entities.ResponseAndToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.example.helpers.entities.TestsByUrlName.REGISTER_USER_METHOD_TESTS_NAME;
import static org.junit.Assert.*;
import static org.example.steps.UserSteps.registerUser;
import static org.example.steps.UserSteps.deleteUser;
import static org.example.helpers.RandomSequences.*;

@Feature(REGISTER_USER_METHOD_TESTS_NAME)
public class RegisterUserTest {

    User user;
    ResponseAndToken responseAndToken;

    @Before
    public void beforeRegisterRandomUser() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        responseAndToken = registerUser(user);
    }

    @Test
    public void registerUserWithRequiredArgsReturnsStatus200Test() {
        assertEquals(SC_OK, responseAndToken.getResponse().statusCode());
    }

    @Test
    public void registerUserWithRequiredArgsReturnsSuccessTrueTest() {
        assertTrue(responseAndToken.getResponse().as(UserLoginResponse.class).isSuccess());
    }

    @Test
    public void registerUserWithRequiredArgsReturnsSuccessTokenTest() {
        assertNotNull(responseAndToken.getResponse().as(UserLoginResponse.class).getAccessToken());
    }

    @Test
    public void registerUserWithRequiredArgsReturnsRefreshTokenTest() {
        assertNotNull(responseAndToken.getResponse().as(UserLoginResponse.class).getRefreshToken());
    }

    @Test
    public void registerUserWithRequiredArgsReturnsUserEmailTest() {
        assertEquals(user.getEmail()
                , responseAndToken.getResponse().as(UserLoginResponse.class).getUser().getEmail());
    }

    @Test
    public void registerUserWithRequiredArgsReturnsUserNameTest() {
        assertEquals(user.getName()
                , responseAndToken.getResponse().as(UserLoginResponse.class).getUser().getName());
    }

    @Test
    public void registerUserByExistingUserDataReturns403Test() {
        responseAndToken = registerUser(user);
        assertEquals(SC_FORBIDDEN, responseAndToken.getResponse().getStatusCode());
        assertFalse(responseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("User already exists"
                , responseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @After
    public void afterDeleteUser() {
        if (responseAndToken.getAuthToken() != null)
            deleteUser(responseAndToken.getAuthToken());
    }
}
