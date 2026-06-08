// config/Constants.java
package config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

public class Constants {

    @Config.Sources("classpath:constants.properties")

    interface Props extends Config {
        @Key("server.url")
        String serverUrl();

        @Key("login.user")
        String loginUser();

        @Key("login.password")
        String loginPassword();

        @Key("openrouter.url")
        @DefaultValue("https://openrouter.ai/api/v1/chat/completions")
        String openrouterUrl();

        @Key("openrouter.model")
        @DefaultValue("openai/gpt-oss-120b:free")
        String openrouterModel();

        @Key("openrouter.api.key")
        @DefaultValue("")
        String openrouterApiKey();
    }

    private static final Props props;

    static {
        props = ConfigFactory.create(Props.class);
        // Диагностический вывод – убеждаемся, что ключ загрузился
        System.out.println("[Constants] server  = " + props.serverUrl());
        System.out.println("[Constants] API_KEY = " + (props.openrouterApiKey().isEmpty() ? "ПУСТО" : "***"));
    }

    public static class Servers {
        public static String PDS_VITA_TEST_URL = props.serverUrl();
    }

    public static class Login {
        public static String PDS_VITA_TEST_LOGIN = props.loginUser();
    }

    public static class Password {
        public static String PDS_VITA_TEST_PASSWORD = props.loginPassword();
    }

    public static class OpenRouter {
        public static String URL = props.openrouterUrl();
        public static String MODEL = props.openrouterModel();
        public static String API_KEY = props.openrouterApiKey();
    }

    public static class RunVeriable {
        public static String server = Servers.PDS_VITA_TEST_URL;
        public static String login = Login.PDS_VITA_TEST_LOGIN;
        public static String password = Password.PDS_VITA_TEST_PASSWORD;
    }
}