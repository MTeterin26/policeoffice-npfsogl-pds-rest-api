package tests;

import api.LoginApiClient;
import config.Config;
import org.junit.jupiter.api.BeforeAll;
import setup.TestDataInitializer;

public abstract class BaseTest {

    @BeforeAll
    public static void globalSetup() {
        new Config().setUp();               // baseURI один раз
        LoginApiClient.loginIfNeeded();     // получаем токен один раз
        // Инициализация данных от AI — выполнится один раз перед всеми тестами
        TestDataInitializer.initialize();
    }
}