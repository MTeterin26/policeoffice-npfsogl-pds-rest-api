package api;

import config.Config;
import io.restassured.http.ContentType;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Calculet {

    private Config config = new Config();

    @Before
    public void setup() {
        config.setUp();
        // Если токен ещё не получен — получаем его
        if (Config.sessionToken == null) {
            Login login = new Login();
            login.setUp();
            login.performLogin();
        }
    }

    /**
     * Метод для получения тела запроса calculate с параметрами.
     */
    public String getBodyCalculet(String risksName, String ageIntValue) throws Exception {
        String bodyCalculet = Files.readString(Path.of("src/body/bodyCalculet.json"));

        Map<String, String> values = Map.of(
                "risksName", risksName,
                "ageIntValue", ageIntValue
        );

        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        return substitutor.replace(bodyCalculet);
    }

    /**
     * Выполняет расчёт и возвращает ID калькуляции (calcID).
     */
    public String getCalcID(String risksName, String ageIntValue) throws Exception {
        String body = getBodyCalculet(risksName, ageIntValue);

        String calcID = given()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(body)
                .when()
                .post("/PO.Insurance/services/partner/v1/calculate")
                .then()
                .statusCode(200)
                .extract()
                .path("calcPolicyResult.calcResults[0].policy.calcID");

        return calcID;
    }

    /**
     * Выполняет расчёт и возвращает ID полиса (policyID).
     */
    public String getPolicyID(String risksName, String ageIntValue) throws Exception {
        String body = getBodyCalculet(risksName, ageIntValue);

        return given()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(body)
                .when()
                .post("/PO.Insurance/services/partner/v1/calculate")
                .then()
                .statusCode(200)
                .extract()
                .path("calcPolicyResult.calcResults[0].policy.ID");
    }

    @Test
    public void calculet1() throws Exception {
        String resultBodyCalculet = getBodyCalculet("Пенсионное накопление", "45");

        given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(resultBodyCalculet)
                .when()
                .post("/PO.Insurance/services/partner/v1/calculate")
                .then().log().body()
                .statusCode(200);
    }

    @Test
    public void calculet2() throws Exception {
        String resultBodyCalculet = getBodyCalculet("Пенсионное накопление", "30");

        given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(resultBodyCalculet)
                .when()
                .post("/PO.Insurance/services/partner/v1/calculate")
                .then().log().ifValidationFails()
                .statusCode(200);
    }
}