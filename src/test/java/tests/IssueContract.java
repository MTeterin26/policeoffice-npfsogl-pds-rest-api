package tests;

import api.CalculateApiClient;
import api.ImportApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import setup.TestDataInitializer;
import setup.TestDataInitializer.TestUser;
import utils.JsonFileReader;

import static org.hamcrest.Matchers.notNullValue;

public class IssueContract extends BaseTest {

    private final CalculateApiClient calcApi = new CalculateApiClient();
    private final ImportApiClient importApi = new ImportApiClient();

    @Test
    @Tag("smoke")
    @Tag("regression")
    public void scenario1_issueAfterCalculate() {
        TestUser user = TestDataInitializer.USERS.get(5);

        // 1. Расчёт
        String calcBody = JsonFileReader.readAsString("payloads/calculate/bodyCalculet.json");
        Response calcResp = calcApi.calculate(calcBody);
        calcResp.then()
                .statusCode(200)
                .body("calcPolicyResult.calcResults[0].policy.calcID", notNullValue())
                .body("calcPolicyResult.calcResults[0].policy.ID", notNullValue());
        String calcID = calcResp.path("calcPolicyResult.calcResults[0].policy.calcID");
        String policyID = calcResp.path("calcPolicyResult.calcResults[0].policy.ID");
        System.out.println("Забрали параметры из метода calculate — calcID: " + calcID + ", policyID: " + policyID);

        // 2. Импорт (calcID передаётся)
        String importBody = buildImportBody(user, calcID);
        Response importResp = importApi.importPolicy(importBody);
        importResp.then()
                .statusCode(200)
                .body("policy.ID", notNullValue())
                .body("policy.calcID", notNullValue());
        String importPolicyID = importResp.path("policy.ID");
        String importCalcID = importResp.path("policy.calcID");
        System.out.println("Забрали параметры из метода import — calcID: " + importCalcID + ", policyID: " + importPolicyID);

        // 3. Прикрепляем документы
        attachDocuments(importCalcID);

        // 4. Оформляем договор
        issueAndCheck(importPolicyID);
        System.out.println("Сценарий 1 завершён — договор оформлен");
    }

    @Test
    @Tag("regression")
    public void scenario2_issueAfterImport() {
        TestUser user = TestDataInitializer.USERS.get(6);

        // 1. Импорт (calcID не передаётся)
        String importBody = buildImportBody(user, null);
        Response importResp = importApi.importPolicy(importBody);
        importResp.then()
                .statusCode(200)
                .body("policy.ID", notNullValue())
                .body("policy.calcID", notNullValue());
        String importPolicyID = importResp.path("policy.ID");
        String importCalcID = importResp.path("policy.calcID");
        System.out.println("Забрали параметры из метода import — calcID: " + importCalcID + ", policyID: " + importPolicyID);

        // 2. Прикрепляем документы
        attachDocuments(importCalcID);

        // 3. Оформляем договор
        issueAndCheck(importPolicyID);
        System.out.println("Сценарий 2 завершён — договор оформлен");
    }
}