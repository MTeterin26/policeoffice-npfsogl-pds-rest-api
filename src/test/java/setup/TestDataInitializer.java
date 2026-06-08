package setup;

import api.AiDataProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import utils.JsonFileReader;

import java.util.ArrayList;
import java.util.List;

public class TestDataInitializer {

    /**
     * Сгенерированные пользователи.
     * Каждый пользователь — это одна комбинация ИНН, СНИЛС и ФИО.
     */
    public static final List<TestUser> USERS = new ArrayList<>();

    /**
     Инициализация — вызывается один раз перед Suite.
     Если AI недоступен или вернул некорректный ответ,
     метод бросает исключение — тесты не запустятся.
     */
    public static void initialize() {
        try {
            // 1. Загружаем тело запроса из шаблона
            String aiRequestBody = JsonFileReader.readAsString(
                    "payloads/aiDataProvider/aiDataProvider_prompt.json"
            );

            // 2. Отправляем запрос к ИИ
            AiDataProvider ai = new AiDataProvider();
            Response aiResponse = ai.generate(aiRequestBody);

            // 3. Вытаскиваем сгенерированный JSON из ответа
            String raw = aiResponse.jsonPath().getString("choices[0].message.content");

            // 4. Убираем markdown-обёртку (```json ... ```) если она есть
            String cleanJson = raw
                    .replaceAll("(?i)```json\\s*", "")
                    .replaceAll("(?i)```", "")
                    .trim();

            // 5. Парсим чистый JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(cleanJson);

            // 6. Извлекаем списки
            List<String> innList = extractList(root, "parametersINN");
            List<String> snilsList = extractList(root, "parametersSNILS");
            List<String> lastNameList = extractList(root, "parametersLastName");
            List<String> firstNameList = extractList(root, "parametersFirstName");
            List<String> middleNameList = extractList(root, "parametersMiddleName");


            //7. Собираем пользователей (по минимальному размеру списков)
            int size = Math.min(innList.size(),
                    Math.min(snilsList.size(),
                            Math.min(lastNameList.size(),
                                    Math.min(firstNameList.size(), middleNameList.size()))));
            for (int i = 0; i < size; i++) {
                USERS.add(new TestUser(
                        innList.get(i),
                        snilsList.get(i),
                        lastNameList.get(i),
                        firstNameList.get(i),
                        middleNameList.get(i)
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Не удалось инициализировать тестовые данные от AI. Причина: " + e.getMessage(), e
            );
        }
    }

    /**
    Извлекает список строк из поля data внутри объекта.
    Ожидается структура: {"fieldName": { "data": ["значение1", "значение2", ...] }}
     */
    private static List<String> extractList(JsonNode root, String fieldName) {
        List<String> list = new ArrayList<>();
        JsonNode dataArray = root.at("/" + fieldName + "/data");
        if (dataArray != null && dataArray.isArray()) {
            for (JsonNode item : dataArray) {
                list.add(item.asText());
            }
        }
        return list;
    }

    public record TestUser(String inn, String snils, String lastName, String firstName, String middleName) {}
}