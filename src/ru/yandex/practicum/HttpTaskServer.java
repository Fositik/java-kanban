package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.model.FileBackedTasksManager;
import ru.yandex.practicum.service.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

//Сначала добавьте в проект библиотеку Gson для работы с JSON.
//Добавьте в него реализацию FileBackedTaskManager, которую можно получить из утилитного класса Managers.
// После этого можно реализовать маппинг запросов на методы интерфейса TaskManager.
public class HttpTaskServer extends FileBackedTasksManager {
    //Далее создайте класс HttpTaskServer, который будет слушать порт 8080 и принимать запросы.
    private static final int PORT = 8080;
    private HttpServer server;
    private static final String PATH_FILE = "src/programTesting.csv";

    private final Gson gson = new GsonBuilder().create();

    public HttpTaskServer() {
        super(PATH_FILE);
    }

    public void runServer() {
        try { // Unhandled exception: java.io.IOException
            server = HttpServer.create(); // создали веб-сервер
            server.bind(new InetSocketAddress(PORT), 0); // привязали его к порту
        } catch (IOException e) {
            e.getMessage();
        }
        server.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");

    }

    public void stopServer() {
        System.out.println("Завершение работы сервера на порту " + PORT);
        server.stop(0);
    }

    // API должен работать так, чтобы все запросы по пути /tasks/<ресурсы> приходили в интерфейс TaskManager.
    // Путь для обычных задач — /tasks/task, для подзадач — /tasks/subtask, для эпиков — /tasks/epic.
    // Получить все задачи сразу можно будет по пути /tasks/, а получить историю задач по пути /tasks/history.
    private void workingOnSimpleTasks() {
        server.createContext("/tasks/task", (HttpExchange httpExchange) -> {////Путь для обычных задач — /tasks/task
            String method = httpExchange.getRequestMethod(); //
            String response = null;
            String taskIdInfo = null;
            switch (method) {
                case "GET": //Для получения данных должны быть GET-запросы.
                    /**
                     * Функция getRawQuery () является частью класса URI.
                     * Функция getRawQuery () возвращает необработанный запрос указанного URI.
                     * Эта функция возвращает точное значение запроса без декодирования последовательности
                     * экранированных октетов, если таковые имеются.
                     */
                    taskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    // System.out.println(taskIdInfo);
                    if (taskIdInfo != null) {
                        int taskId = getValueOfId(taskIdInfo);
                        response = getSimpleTaskById(taskId).toString();
                    } else {
                        getAllSimpleTasks().toString();
                    }
                    printResponse(response, httpExchange);
                case "POST": //Для создания и изменения — POST-запросы.
                    //Так как метод getRequestBody() возвращает InputStream, входящий поток необходимо обработать.
                    // Можно, например, считать из него массив байтов методом inputStream.readAllBytes(),
                    // а затем с помощью конструктора String сконвертировать в строковый тип
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), UTF_8);
                    Task newTask = gson.fromJson(jsonString, Task.class);
                    if (!simpleTasks.containsKey(newTask.getId())) {
                        addSimpleTask(newTask);
                        response = String.format("Задача под id = %d успешно создана", newTask.getId());
                    } else {
                        updateSimpleTaskById(newTask.getId(), newTask);
                        response = String.format("Задача под id = %d успешно обновлена", newTask.getId());
                    }
                    printResponse(response, httpExchange);
                case "DELETE": // Для удаления — DELETE-запросы.
                    taskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (taskIdInfo != null) {
                        int simpleTAskId = getValueOfId(taskIdInfo);
                        removeSimbletaskById(simpleTAskId);
                        response = String.format("Задача под id = %d удалена", simpleTAskId);
                    }
                    printResponse(response, httpExchange);
            }

        });
    }

    private int getValueOfId(String infOfId) {
        String[] infOfIfSplit = infOfId.split("=");
        return Integer.parseInt(infOfIfSplit[1]);
    }

    private void printResponse(String response, HttpExchange httpExchange) {
        try { // Unhandled exception: java.io.IOException
            if (response != null) {
                httpExchange.sendResponseHeaders(200, 0); //Метод отправляет ответ, который можно сформировать заранее.
                //Если вместе с ответом необходимо передать какие-либо данные, нужно получить экземпляр класса OutputStream, связанный с телом ответа.
                try (OutputStream os = httpExchange.getResponseBody()) { //Необходимо вызвать метод getResponseBody() класса HttpExchange
                    //Класс OutputStream позволяет записывать данные в виде массива байтов. Для этого нужно вызвать метод write(byte b[])
                    os.write(response.getBytes());
                }
            } else {
                httpExchange.sendResponseHeaders(400, 0);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
