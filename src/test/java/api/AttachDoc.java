package api;

import config.Config;
import io.restassured.http.ContentType;
import org.junit.Before;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AttachDoc {

    private Config config = new Config();

    @Before
    public void setup() {
        config.setUp();
        if (Config.sessionToken == null) {
            Login login = new Login();
            login.setUp();
            login.performLogin();
        }
    }

    /**
     * Прикрепляет документ к договору.
     *
     * @param calcID   ID калькуляции
     * @param fileName имя файла
     * @param type     тип документа
     * @param content  содержимое файла в Base64
     */
    public void attachDocument(String calcID, String fileName, String type, String content) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("calcID", calcID);
        requestBody.put("fileName", fileName);
        requestBody.put("type", type);
        requestBody.put("attachment", content);

        given()
                .log().ifValidationFails()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(requestBody)
                .when()
                .post("/PO.Insurance/services/partner/v1/attachDoc")
                .then()
                .log().ifValidationFails()
                .statusCode(200);

        System.out.println("Документ прикреплён: " + type + " (calcID: " + calcID + ")");
    }

    /**
     * Прикрепляет тестовый документ с содержимым "Тестовый документ".
     */
    public void attachTestDocument(String calcID, String type) {
        String content = Base64.getEncoder().encodeToString("Тестовый документ".getBytes());
        attachDocument(calcID, "test.txt", type, content);
    }

    /**
     * Прикрепляет обязательный набор документов для оформления.
     */
    public void attachRequiredDocuments(String calcID) {
        System.out.println("=== Прикрепление документов для calcID: " + calcID + " ===");

        // Список обязательных документов из ошибки API
        attachTestDocument(calcID, "Документ, удостоверяющий личность");
        attachTestDocument(calcID, "Анкета для проведения идентификации клиента");
        attachTestDocument(calcID, "Согласие на обработку ПД");
        attachTestDocument(calcID, "Согласие на доп. услугу");

        System.out.println("=== Все документы прикреплены ===");
    }
}