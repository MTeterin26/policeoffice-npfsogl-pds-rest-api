package tests;

import api.AttachDocApiClient;
import api.IssueApiClient;
import api.LoginApiClient;
import config.Config;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import setup.TestDataInitializer;
import utils.JsonFileReader;
import utils.TemplateResolver;
import utils.TestDocumentUtils;

import static org.hamcrest.Matchers.notNullValue;

public abstract class BaseTest {

    public final AttachDocApiClient attachApi = new AttachDocApiClient();
    public final IssueApiClient issueApi = new IssueApiClient();

    @BeforeAll
    public static void globalSetup() {
        new Config().setUp();               // baseURI один раз
        LoginApiClient.loginIfNeeded();     // получаем токен один раз
        // Инициализация данных от AI — выполнится один раз перед всеми тестами
        TestDataInitializer.initialize();
    }

    // Прикрепляем четыре обязательных документа к расчёту. Метод
    public void attachDocuments(String calcID) {
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
    public void issueAndCheck(String policyID) {
        Response issueResp = issueApi.issuePolicy(policyID);
        issueResp.then()
                .statusCode(200)
                .body("policyID", notNullValue());
    }

    // Собирает тело запроса импорта, подставляя данные и опциональный calcID.
    public String buildImportBody(TestDataInitializer.TestUser user, String calcID) {
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
}