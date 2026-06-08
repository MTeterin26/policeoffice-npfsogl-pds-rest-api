// config/Config.java – без изменений
package config;

import io.restassured.RestAssured;

public class Config {

    public void setUp() {
        RestAssured.baseURI = Constants.RunVeriable.server;
    }

    public static String login = Constants.RunVeriable.login;
    public static String password = Constants.RunVeriable.password;
    public static String sessionToken;

    public static String urlAI = Constants.OpenRouter.URL;
    public static String modelAI = Constants.OpenRouter.MODEL;
    public static String APIKeyAI = Constants.OpenRouter.API_KEY;
}