package api;

import config.Config;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class Issue {

    private Config config = new Config();
    private Import importApi = new Import();
    private AttachDoc attachDoc = new AttachDoc();

    @Before
    public void setup() {
        config.setUp();
        if (Config.sessionToken == null) {
            Login login = new Login();
            login.setUp();
            login.performLogin();
        }
        importApi.setup();
        attachDoc.setup();
    }

    /**
     * Тест 1: оформление договора.
     * Шаги: импорт → attachDoc (к calcID из импорта) → issue
     */
    @Test
    public void issueFromCalculate() throws Exception {
        // 1. Выполняем импорт и получаем сразу оба ID
        Map<String, String> importResult = importApi.getImportResult("Пенсионное накопление", "45", "25", "10");
        String policyID = importResult.get("policyID");
        String calcID = importResult.get("calcID");
        System.out.println("policyID: " + policyID);
        System.out.println("calcID: " + calcID);

        // 2. Прикрепляем документы к calcID из импорта
        attachDoc.attachRequiredDocuments(calcID);

        // 3. Оформляем договор
        issuePolicy(policyID);
    }

    /**
     * Тест 2: оформление договора.
     */
    @Test
    public void issueFromImport() throws Exception {
        Map<String, String> importResult = importApi.getImportResult("Пенсионное накопление", "45", "25", "10");
        String policyID = importResult.get("policyID");
        String calcID = importResult.get("calcID");

        attachDoc.attachRequiredDocuments(calcID);
        issuePolicy(policyID);
    }

    /**
     * Общий метод для оформления договора по policyID.
     */
    private void issuePolicy(String policyID) {
        Map<String, String> requestBody = Map.of("policyID", policyID);

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Date", "Mon, 30 Aug 2021 13:35:03 GMT")
                .header("sessionToken", Config.sessionToken)
                .body(requestBody)
                .when()
                .post("/PO.Insurance/services/partner/v1/issue")
                .then()
                .log().body()
                .statusCode(200);

        System.out.println("Договор успешно оформлен. policyID: " + policyID);
    }
}