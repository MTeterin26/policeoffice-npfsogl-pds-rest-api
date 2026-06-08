package utils;

public class TemplateResolver {

    /**
     * Удаляет из JSON все оставшиеся плейсхолдеры вида "ключ": "{{значение}}"
     * вместе с запятой, если она была перед или после, чтобы JSON остался валидным.
     */

    public static String removeUnusedPlaceholders(String json) {
        return json.replaceAll("\\s*\"[^\"]+\"\\s*:\\s*\"\\{\\{[^}]+}}\"\\s*,?\\s*", "")
                .replaceAll(",\\s*}", "}")
                .replaceAll(",\\s*]", "]");
    }
}
