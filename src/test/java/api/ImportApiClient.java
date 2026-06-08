package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ImportApiClient {

    public Response importPolicy(String requestBody) {
        return given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(requestBody)
                .post("/PO.Insurance/services/partner/v1/import")
                .then().log().ifValidationFails()
                .extract().response();
    }

}
