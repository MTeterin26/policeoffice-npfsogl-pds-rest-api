package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class IssueApiClient {

    public Response issuePolicy(String policyID) {
        return given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(Map.of("policyID", policyID))
                .post("/PO.Insurance/services/partner/v1/issue")
                .then().log().ifValidationFails()
                .extract().response();
    }
}
