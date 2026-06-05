package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Import {

    private Config config = new Config();
    private Calculet calculet = new Calculet();

    @Before
    public void setup() {
        config.setUp();
        if (Config.sessionToken == null) {
            Login login = new Login();
            login.setUp();
            login.performLogin();
        }
        calculet.setup();
    }

    /**
     * Выполняет импорт и возвращает результат в виде Map с ключами "policyID" и "calcID".
     * Один запрос API — гарантирует, что calcID и policyID связаны.
     */
    public Map<String, String> getImportResult(String risksName, String ageIntValue,
                                               String srokNakoplenij, String srokEzhemesVyplat) throws Exception {
        // 1. Получаем calcID из предварительного расчёта
        String calcID = calculet.getCalcID(risksName, ageIntValue);
        System.out.println("Получен calcID из расчёта: " + calcID);

        // 2. Читаем шаблон тела импорта
        String bodyImport = Files.readString(Path.of("src/body/bodyImport.json"));

        // 3. Подставляем параметры
        Map<String, String> values = Map.of(
                "ageIntValue", ageIntValue,
                "srokNakoplenij", srokNakoplenij,
                "srokEzhemesVyplat", srokEzhemesVyplat
        );

        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        String resultBodyImport = substitutor.replace(bodyImport);

        // 4. Отправляем запрос на импорт
        Response response = given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(resultBodyImport)
                .when()
                .post("/PO.Insurance/services/partner/v1/import")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .response();

        // 5. Извлекаем оба поля из одного ответа
        String policyID = response.jsonPath().getString("policy.ID");
        String calcIDFromImport = response.jsonPath().getString("policy.calcID");

        System.out.println("Получен policyID: " + policyID);
        System.out.println("Получен calcID из импорта: " + calcIDFromImport);

        Map<String, String> result = new HashMap<>();
        result.put("policyID", policyID);
        result.put("calcID", calcIDFromImport);
        return result;
    }

    /**
     * Выполняет импорт и возвращает policyID.
     */
    public String getPolicyID(String risksName, String ageIntValue,
                              String srokNakoplenij, String srokEzhemesVyplat) throws Exception {
        return getImportResult(risksName, ageIntValue, srokNakoplenij, srokEzhemesVyplat).get("policyID");
    }

    /**
     * Выполняет импорт и возвращает calcID из ответа.
     */
    public String getCalcIDFromImport(String risksName, String ageIntValue,
                                      String srokNakoplenij, String srokEzhemesVyplat) throws Exception {
        return getImportResult(risksName, ageIntValue, srokNakoplenij, srokEzhemesVyplat).get("calcID");
    }

    @Test
    public void importPolicy() throws Exception {
        Map<String, String> result = getImportResult("Пенсионное накопление", "45", "25", "10");
        System.out.println("Импорт успешен. policyID: " + result.get("policyID"));
    }
}