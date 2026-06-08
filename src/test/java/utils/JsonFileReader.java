package utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonFileReader {

    public static String readAsString(String resourcePath) {
        /**
         * Читает файл из classpath и возвращает его содержимое как строку в UTF-8.
         *
         * @param resourcePath путь относительно корня classpath, например "payloads/user/createUser_template.json"
         * @return содержимое файла
         * @throws RuntimeException если файл не найден или произошла ошибка чтения
         */

        try (InputStream is = JsonFileReader.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Файл не найден в classpath: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения файла: " + resourcePath, e);
        }
    }
}