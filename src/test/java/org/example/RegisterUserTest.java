package org.example;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
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
    ResponseAndToken registerResponseAndToken;
    ResponseAndToken repeatedRegisterResponseAndToken;

    @Before
    public void beforeRegisterRandomUser() {
        user = new User(getRandomEmail(), createRandomPassword(8), getRandomName());
        registerResponseAndToken = registerUser(user);
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

    @Test
    @DisplayName("При попытке регистрации по уже существующим данным пользователя статус код ответа 403")
    public void registerUserByExistingUserDataReturns403Test() {
        repeatedRegisterResponseAndToken = registerUser(user);
        assertEquals(SC_FORBIDDEN, repeatedRegisterResponseAndToken.getResponse().getStatusCode());
        assertFalse(repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("User already exists"
                , repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @Test
    @DisplayName("При попытке регистрации с уже существующим значением email статус код ответа 403")
    public void registerUserWithExistingUserEmailReturns403Test() {
        repeatedRegisterResponseAndToken = registerUser(new User(user.getEmail(), createRandomPassword(8), getRandomName()));
        assertEquals(SC_FORBIDDEN, repeatedRegisterResponseAndToken.getResponse().getStatusCode());
        assertFalse(repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).isSuccess());
        assertEquals("User already exists"
                , repeatedRegisterResponseAndToken.getResponse().as(ErrorMessageResponse.class).getMessage());
    }

    @After
    public void afterDeleteUser() {
        if (registerResponseAndToken.getAuthToken() != null) deleteUser(registerResponseAndToken.getAuthToken());
        // Удалить пользователя, если он был создан с использованием данных существующего пользователя
        if (repeatedRegisterResponseAndToken != null) {
            if (repeatedRegisterResponseAndToken.getAuthToken() != null)
                deleteUser(registerResponseAndToken.getAuthToken());
        }
    }

}
