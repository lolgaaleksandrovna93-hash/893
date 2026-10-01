package ru.netology.rest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class MobileBankApiTestV1 {
    @Test
    void shouldReturnDemoAccounts() {
        // Given - When - Then
        // Предусловия
        given()
                .baseUri("http://localhost:9999/api/v1")
                // Выполняемые действия: меняем путь на несуществующий
                .when()
                .get("/demo/accounts-wrong-path")
                // Проверки
                .then()
                .statusCode(200); // Ожидается 200, но придёт 404 -> тест упадёт
    }
}
