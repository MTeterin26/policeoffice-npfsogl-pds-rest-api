package utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TestDocumentUtils {

    /** Текст документа по умолчанию, если не передан другой */
    public static final String DEFAULT_CONTENT = "Тестовый документ";

    /**
     * Создаёт Base64-строку для текста по умолчанию.
     */
    public static String createBase64Content() {
        return createBase64Content(DEFAULT_CONTENT);
    }

    /**
     * Создаёт Base64-строку для произвольного текста.
     */
    public static String createBase64Content(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
