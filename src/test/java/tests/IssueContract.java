package tests;

import api.AttachDocApiClient;
import api.CalculateApiClient;
import api.ImportApiClient;
import api.IssueApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import setup.TestDataInitializer;
import setup.TestDataInitializer.TestUser;
import utils.JsonFileReader;
import utils.TemplateResolver;
import utils.TestDocumentUtils;

import static org.hamcrest.Matchers.notNullValue;

public class IssueContract extends BaseTest {

    private final CalculateApiClient calcApi = new CalculateApiClient();
    private final ImportApiClient importApi = new ImportApiClient();
    private final AttachDocApiClient attachApi = new AttachDocApiClient();
    private final IssueApiClient issueApi = new IssueApiClient();

    // Собирает тело запроса импорта, подставляя данные и опциональный calcID.
    private String buildImportBody(TestUser user, String calcID) {
        String template = JsonFileReader.readAsString("payloads/import/bodyImport.json");
        String body = template
                .replace("{{inn}}", user.inn())
                .replace("{{snils}}", user.snils())
                .replace("{{lastName}}", user.lastName())
                .replace("{{firstName}}", user.firstName())
                .replace("{{middleName}}", user.middleName());
        if (calcID != null) {
            body = body.replace("{{calcID}}", calcID);
        }
        return TemplateResolver.removeUnusedPlaceholders(body);
    }

    // Прикрепляем четыре обязательных документа к расчёту.
    private void attachDocuments(String calcID) {
        String content = TestDocumentUtils.createBase64Content();
        attachApi.attachDocument(calcID, "test.txt", "Документ, удостоверяющий личность", content)
                .then().statusCode(200);
        attachApi.attachDocument(calcID, "test.txt", "Анкета для проведения идентификации клиента", content)
                .then().statusCode(200);
        attachApi.attachDocument(calcID, "test.txt", "Согласие на обработку ПД", content)
                .then().statusCode(200);
        attachApi.attachDocument(calcID, "test.txt", "Согласие на доп. услугу", content)
                .then().statusCode(200);
    }

    //Выпуск договора
    private void issueAndCheck(String policyID) {
        Response issueResp = issueApi.issuePolicy(policyID);
        issueResp.then()
                .statusCode(200)
                .body("policyID", notNullValue());
    }

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