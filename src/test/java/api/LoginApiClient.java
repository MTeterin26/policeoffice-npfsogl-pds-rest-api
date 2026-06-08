package api;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;

public class LoginApiClient {

    // Однократный логин
    public static synchronized void loginIfNeeded() {
        if (Config.sessionToken != null) {
            System.out.println("Токен уже существует: " + Config.sessionToken);
            return;
        }

        String token = RestAssured
                .given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic d3NvMnJlbmluczpwKXgiRCpcOEwqcj9eKzJc")
                .header("klient_login", Config.login)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .body(Map.of("login", Config.login, "password", Config.password))
                .when()
                .post("/PO.Insurance/services/partner/v1/auth/login")
                .then().log().ifValidationFails()
                .extract()
                .path("sessionToken");

        Config.sessionToken = token;
        System.out.println("Сохранен токен авторизации");
    }
}
