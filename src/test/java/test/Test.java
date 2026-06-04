package test;

import config.Config;
import io.restassured.http.ContentType;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Before;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Test {

    private Config config = new Config();

    @Before
    public void setup(){
        config.setUp();
        // Если токен ещё не получен — получаем его
        if (Config.sessionToken == null) {
            Login login = new Login();
            login.setUp();
            login.performLogin();
        }
    }

    @org.junit.Test
    public void calculet() throws Exception{

        String bodyCalculet = Files.readString(
                Path.of("src/body/bodyCalculet.json")
        );

        //Параметризация
        Map<String, String> values = Map.of(
                "risksName", "Пенсионное накопление",
                "ageIntValue", "30"
        );

        StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
        String resultBodyCalculet = substitutor.replace(bodyCalculet);

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
