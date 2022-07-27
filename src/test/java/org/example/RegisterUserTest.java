package org.example;

import org.example.entities.User;
import org.example.entities.UserRegisterResponse;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.example.steps.UserSteps.*;
import static org.example.helpers.RandomSequences.*;

public class RegisterUserTest {

    @Test
    public void registerUserWIthAllRequiredParamsOKTest() {
        UserRegisterResponse response = registerUser(new User(getRandomEmail(), createRandomPassword(8), getRandomName()))
                .as(UserRegisterResponse.class);
        assertEquals(response.isSuccess(), true);
    }
}
