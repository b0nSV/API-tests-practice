package org.example.helpers.entities;

import static org.example.steps.UserSteps.LOGIN_COURIER_URL;
import static org.example.steps.UserSteps.REGISTER_COURIER_URL;
import static org.example.steps.UserSteps.PATCH_COURIER_URL;
import static org.example.steps.OrderSteps.CREATE_ORDER_URL;

public interface TestsByUrlName {
    String LOGIN_USER_METHOD_TESTS_NAME = "Выполнить вход - POST " + LOGIN_COURIER_URL;
    String REGISTER_USER_METHOD_TESTS_NAME = "Регистрация пользователя - POST " + REGISTER_COURIER_URL;
    String PARTIAL_UPDATE_USER_METHOD_TESTS_NAME = "Частичное обновление данных пользователя - PATCH " + PATCH_COURIER_URL;
    String CREATE_ORDER_METHOD_TESTS_NAME = "Создание заказа - POST " + CREATE_ORDER_URL;
}
