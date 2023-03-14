import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.gson.deserialize.DurationJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.EpicJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.SubtaskJsonDeserializer;
import ru.yandex.practicum.http.server.HttpTaskServer;
import ru.yandex.practicum.http.server.KVServer;
import ru.yandex.practicum.service.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static HttpTaskServer httpTaskServer;
    private static KVServer kvServer;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Subtask.class, new SubtaskJsonDeserializer())
            .registerTypeAdapter(Epic.class, new EpicJsonDeserializer())
            .registerTypeAdapter(Duration.class, new DurationJsonDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static final String TASKS_TASK = "http://localhost:8080/tasks/task/";
    private static final String TASKS_EPIC = "http://localhost:8080/tasks/epic/";
    private static final String TASKS_SUBTASK = "http://localhost:8080/tasks/subtask/";

    @BeforeAll
    static void runServer() {       //Запускаем сервер
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void resetServer() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            url = URI.create(TASKS_EPIC);
            httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            url = URI.create(TASKS_SUBTASK);
            httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {      //Оставнавливаем сервер
        kvServer.stop();
        httpTaskServer.stopServer();
    }

    @Test
    void shouldGetSimpleTasks() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Task task = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            JsonArray jsonArray = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            assertEquals(1, jsonArray.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
        // HttpClient предоставляет удобный интерфейс для создания HTTP-запросов и обработки ответов.
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );
        //`HttpRequest` - это объект, который представляет HTTP-запрос.
        // Он содержит метод запроса (например, GET или POST), URL-адрес, заголовки и тело запроса.
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            //Эта строка кода отправляет HTTP-запрос и получает ответ с помощью классов `HttpClient` и `HttpResponse`
            httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString()); //возвращает экземпляр класса
            httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            JsonArray jsonArray = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            assertEquals(1, jsonArray.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasksTest() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            //`HttpResponse` представляет HTTP-ответ и содержит информацию, такую как код состояния,
            // заголовки и тело ответа. Класс `HttpResponse` обычно используется для чтения ответа от сервера после
            // отправки запроса с помощью класса `HttpClient` или другой библиотеки.
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ////201 - это обычный код ответа, который означает "Создан".
            assertEquals(201, httpResponse.statusCode(), "POST запрос");
            if (httpResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(httpResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask(
                        "name1",
                        "description1",
                        Status.NEW,
                        LocalDateTime.now(),
                        Duration.ofMinutes(1),
                        epic
                );
                url = URI.create(TASKS_SUBTASK);

                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build(); //теперь постим подзадачу

                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()); //отправляем зпрос
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();  //получаем ответ
                HttpResponse<String> stringHttpResponse = httpClient.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString()
                ); //получаем тело ответа в виде строки
                assertEquals(200, stringHttpResponse.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(stringHttpResponse.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSimpleTaskById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Task task = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create(TASKS_TASK + "?id=" + id);
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpicById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setId(id);
                url = URI.create(TASKS_EPIC + "?id=" + id);
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtaskById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask(
                        "name1",
                        "description1",
                        Status.NEW,
                        LocalDateTime.now(),
                        Duration.ofMinutes(1),
                        epic
                );
                url = URI.create(TASKS_SUBTASK);

                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create(TASKS_SUBTASK + "?id=" + id);
                    httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSimpleTask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Task task = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setStatus(Status.IN_PROGRESS);
                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                url = URI.create(TASKS_TASK + "?id=" + id);
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setStatus(Status.IN_PROGRESS);
                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                        .build();
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                url = URI.create(TASKS_EPIC + "?id=" + id);
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSubtask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask(
                        "description1",
                        "name1",
                        Status.NEW,
                        LocalDateTime.now(),
                        Duration.ofMinutes(1),
                        epic
                );
                url = URI.create(TASKS_SUBTASK);

                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setStatus(Status.IN_PROGRESS);
                    httpRequest = HttpRequest.newBuilder()
                            .uri(url)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                    url = URI.create(TASKS_SUBTASK + "?id=" + id);
                    httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveSimpleTask() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Task task = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, httpResponse.statusCode());
            httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            assertEquals(0, jsonArray.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveEpic() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Epic epic = new Epic(
                "name1",
                "description1",
                Status.NEW
        );
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, httpResponse.statusCode());
            httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, httpResponse.statusCode());
            JsonArray jsonArray = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            assertEquals(0, jsonArray.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveSubtaskTest() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "description1",
                "name1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            //`HttpResponse` представляет HTTP-ответ и содержит информацию, такую как код состояния,
            // заголовки и тело ответа. Класс `HttpResponse` обычно используется для чтения ответа от сервера после
            // отправки запроса с помощью класса `HttpClient` или другой библиотеки.
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ////201 - это обычный код ответа, который означает "Создан".
            assertEquals(201, httpResponse.statusCode(), "POST запрос");
            if (httpResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(httpResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask(
                        "description1",
                        "name1",
                        Status.NEW,
                        LocalDateTime.now(),
                        Duration.ofMinutes(1),
                        epic
                );
                url = URI.create(TASKS_SUBTASK);

                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build(); //теперь постим подзадачу
                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(201, httpResponse.statusCode());

                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()); //отправляем зпрос
                httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();  //получаем ответ
                HttpResponse<String> stringHttpResponse = httpClient.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString()
                ); //получаем тело ответа в виде строки
                assertEquals(204, httpResponse.statusCode());
                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, stringHttpResponse.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(stringHttpResponse.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTaskById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_TASK);
        Task task = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(httpResponse.body());
            url = URI.create(TASKS_TASK + "?id=" + id);
            httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, stringHttpResponse.statusCode());

            httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
            stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = JsonParser.parseString(stringHttpResponse.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpicById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "description1",
                "name1",
                Status.NEW
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, stringHttpResponse.statusCode(), "POST запрос");
            if (stringHttpResponse.statusCode() == 201) {
                int id = Integer.parseInt(stringHttpResponse.body());
                url = URI.create(TASKS_EPIC + "?id=" + id);
                httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, send.statusCode());

                httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                JsonArray arrayTasks = JsonParser.parseString(send.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtaskById() {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(TASKS_EPIC);
        Epic epic = new Epic(
                "description1",
                "name1",
                Status.NEW
        );
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, httpResponse.statusCode(), "POST запрос");
            if (httpResponse.statusCode() == 201) {
                Subtask subtask = new Subtask(
                        "description1",
                        "name1",
                        Status.NEW,
                        LocalDateTime.now(),
                        Duration.ofMinutes(1),
                        epic
                );
                url = URI.create(TASKS_SUBTASK);

                httpRequest = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, httpResponse.statusCode(), "POST запрос");
                if (httpResponse.statusCode() == 201) {
                    int id = Integer.parseInt(httpResponse.body());
                    subtask.setId(id);
                    url = URI.create(TASKS_SUBTASK + "?id=" + id);
                    httpRequest = HttpRequest.newBuilder().uri(url).DELETE().build();
                    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    assertEquals(204, response.statusCode());

                    httpRequest = HttpRequest.newBuilder().uri(url).GET().build();
                    response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                    JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                    assertEquals(0, arrayTasks.size());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}