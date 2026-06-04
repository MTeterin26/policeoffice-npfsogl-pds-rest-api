package config;

public class Constants {

    public static class Servers {
        public static String PDS_VITA_TEST_URL = "https://preprod.soglasie-vita.ru";
    }

    public static class Login {
        public static String PDS_VITA_TEST_LOGIN = "tarifvita";
    }

    public static class Password {
        public static String PDS_VITA_TEST_PASSWORD = "tarifvita";
    }

    public static class RunVeriable {
        public static String server = Servers.PDS_VITA_TEST_URL;
        public static String login = Login.PDS_VITA_TEST_LOGIN;
        public static String password = Password.PDS_VITA_TEST_PASSWORD;
    }
}