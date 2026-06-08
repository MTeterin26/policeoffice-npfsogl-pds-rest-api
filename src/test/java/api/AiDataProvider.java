package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AiDataProvider {
    public Response generate(String requestBody) {
        return given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + Config.APIKeyAI)
                .body(requestBody)
                .post(Config.urlAI) // полный URL из Config
                .then().log().body()
                .extract().response();
    }
}