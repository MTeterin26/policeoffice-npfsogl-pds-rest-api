package api;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public class Login {
    private Config config = new Config();

    @Before
    public void setUp() {
        config.setUp();
    }

    @Test
    public void login() {
        performLogin();
    }

    public synchronized void performLogin() {
        if (Config.sessionToken != null) {
            System.out.println("Токен уже существует: " + Config.sessionToken);
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("login", Config.login);
        requestBody.put("password", Config.password);

        Response response = RestAssured
                .given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic d3NvMnJlbmluczpwKXgiRCpcOEwqcj9eKzJc")
                .header("klient_login", Config.login)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .body(requestBody)
                .when()
                .post("/PO.Insurance/services/partner/v1/auth/login");

        System.out.println("Ответ от сервера (login)");
        response.then().log().ifValidationFails().statusCode(200);

        Config.sessionToken = response.jsonPath().getString("sessionToken");
        System.out.println("Сохранён токен sessionToken: " + Config.sessionToken);
    }
}