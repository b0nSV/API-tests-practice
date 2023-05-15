# API-testing practice

* [Необходимое ПО для запуска](#Необходимое-ПО-для-запуска)
* [Как запустить тесты](#Как-запустить-тесты)
* [Как получить отчёт Allure](#Как-получить-отчёт-Allure)
* [Библиотеки](#Библиотеки)

## Необходимое ПО для запуска

* Java JDK 17+
* Установленный и прописанный в переменные окружения Gradle
* Для получения отчёта установленный и прописанный в переменные окружения Allure

## Как запустить тесты

* Каждый тестовый класс/тест можно запустить отдельно в `src\test\java`. Для `stand` будет использоваться значение по
  умолчанию `stage`;
* Консольной командой `gradle clean test -Dstand=stage`

## Как получить отчёт Allure

* Сформировать отчёт `gradle allureReport`
* Сформировать отчёт и открыть в браузере `gradle allureServe`

## Библиотеки

* [RestAssured](http://rest-assured.io/) библиотека для тестирования REST APIs
* [JUnit 5](https://junit.org/junit5/) для написания тестов
* [Owner](https://matteobaccan.github.io/owner/) для управления переменными
* [java-faker](https://github.com/DiUS/java-faker) для генерации тестовых данных
* [Allure Report](https://docs.qameta.io/allure/) для визуализации результатов тестов
* [Jackson](https://github.com/FasterXML/jackson) для сериализации/десериализации
* [Lombok](https://projectlombok.org/) чтобы не писать шаблонный код