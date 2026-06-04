package config;

import io.restassured.RestAssured;

public class Config {

    public void setUp() {
        RestAssured.baseURI = Constants.RunVeriable.server;
    }

    public static String login = Constants.RunVeriable.login;
    public static String password = Constants.RunVeriable.password;
    public static String sessionToken;
}