package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class AttachDocApiClient {

    public Response attachDocument(String calcID, String fileName, String type, String contentBase64) {
        Map<String, String> body = Map.of(
                "calcID", calcID,
                "fileName", fileName,
                "type", type,
                "attachment", contentBase64
        );

        return given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(body)
                .post("/PO.Insurance/services/partner/v1/attachDoc")
                .then().log().ifValidationFails()
                .extract().response();
    }
}