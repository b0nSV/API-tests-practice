package site.nomoreparties.stellarburgers.helpers.entities;

import static site.nomoreparties.stellarburgers.steps.OrderSteps.ORDER_URL;
import static site.nomoreparties.stellarburgers.steps.UserSteps.*;

public interface TestsByUrlName {
    String LOGIN_USER_METHOD_TESTS_NAME = "Выполнить вход - POST " + LOGIN_COURIER_URL;
    String REGISTER_USER_METHOD_TESTS_NAME = "Регистрация пользователя - POST " + REGISTER_COURIER_URL;
    String PARTIAL_UPDATE_USER_METHOD_TESTS_NAME = "Частичное обновление данных пользователя - PATCH " + COURIER_URL;
    String CREATE_ORDER_METHOD_TESTS_NAME = "Создание заказа - POST " + ORDER_URL;
    String GET_USER_ORDERS_METHOD_TESTS_NAME = "Получить заказы конкретного пользователя - GET " + ORDER_URL;
}
