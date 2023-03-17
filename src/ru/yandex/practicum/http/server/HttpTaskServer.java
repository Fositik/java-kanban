package ru.yandex.practicum.http.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.gson.deserialize.DurationJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.EpicJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.SubtaskJsonDeserializer;
import ru.yandex.practicum.model.FileBackedTasksManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Содержание класса (чтоб было проще ориентироваться):
 * метод runServer() - для запуска сервера
 * метод stopServer() - для остановки сервера
 * метод workingOnSimpleTasks() - для обработки GET, POST, DELETE запросов простых задач
 * метод workingOnEpics() -  для обработки GET, POST, DELETE запросов эпиков
 * метод workingOnSubtasks() - для обработки GET, POST, DELETE запросов подзадач
 * метод workWithAllSubtasksOfEpic() - для получения всех подзадач определенного эпика (GET запрос)
 * метод getAllTasks() - для получения всех задач (подзадач) в порядке приоритета (getPrioritizedTasks()) (GET запрос)
 * метод workingWithHistory() - для получения истории задач (GET запрос)
 * метод getValueOfId(String infOfId) - для получения id задачи из URI
 * метод printResponse(String response, HttpExchange httpExchange) - метод для формирования ответа сервера
 */
//Сначала добавьте в проект библиотеку Gson для работы с JSON.
//Добавьте в него реализацию FileBackedTaskManager, которую можно получить из утилитного класса Managers.
// После этого можно реализовать маппинг запросов на методы интерфейса TaskManager.
public class HttpTaskServer extends FileBackedTasksManager {
    //Далее создайте класс HttpTaskServer, который будет слушать порт 8080 и принимать запросы.
    private static final int PORT = 8080;
    private HttpServer server;
    private static final String PATH_FILE = "src/FileTest.csv";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Subtask.class, new SubtaskJsonDeserializer())
            .registerTypeAdapter(Epic.class, new EpicJsonDeserializer())
            .registerTypeAdapter(Duration.class, new DurationJsonDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

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
        workingOnSimpleTasks();
        workingOnEpics();
        workingOnSubtasks();
        workWithAllSubtasksOfEpic();
        workingWithHistory();
        getAllTasks();
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
                    //Еще был следующий вариант:
                    //Так как URI имеет следующий вид: ("http://localhost:8080/tasks/task/?id=1"),
                    //Сохранить его в String и засплитить по точке String[] url = ... .split("\\.");
                    //А потом взять последнюю часть получившегося массив: String id = url[utl.length -1]
                    // и запарсить в переменную int;
                    taskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    // System.out.println(taskIdInfo);
                    if (taskIdInfo != null) {
                        int taskId = getValueOfId(taskIdInfo);
                        response = getSimpleTaskById(taskId).toString();
                    } else {
                        getAllSimpleTasks().toString();
                    }
                    printResponse(response, httpExchange);
                    break;
                case "POST": //Для создания и изменения — POST-запросы.
                    //Так как метод getRequestBody() возвращает InputStream, входящий поток необходимо обработать.
                    // Можно, например, считать из него массив байтов методом inputStream.readAllBytes(),
                    // а затем с помощью конструктора String сконвертировать в строковый тип
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), UTF_8);  //Конвертирую поток байтов в String
                    Task newTask = gson.fromJson(jsonString, Task.class);
                    if (!simpleTasks.containsKey(newTask.getId())) {
                        addSimpleTask(newTask);
                        response = String.format("Задача под id = %d успешно создана", newTask.getId());
                    } else {
                        updateSimpleTaskById(newTask.getId(), newTask);
                        response = String.format("Задача под id = %d успешно обновлена", newTask.getId());
                    }
                    printResponse(response, httpExchange);
                    break;
                case "DELETE": // Для удаления — DELETE-запросы.
                    taskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (taskIdInfo != null) {
                        int simpleTAskId = getValueOfId(taskIdInfo);
                        removeSimbletaskById(simpleTAskId);
                        response = String.format("Задача под id = %d удалена", simpleTAskId);
                    }
                    printResponse(response, httpExchange);
                    break;
            }

        });
    }

    private void workingOnEpics() {
        server.createContext("/tasks/epic", (httpExchange) -> {    // для эпиков — /tasks/epic
            String method = httpExchange.getRequestMethod();
            String response = null; //инициализируем сразу, чтобы избежать случая, когда в методе POST данная срока не проинициализируется
            String epicIdInfo = null;
            switch (method) {
                case "GET":
                    //Метод getRawQuery() возвращает строку SQL-запроса, который еще не был скомпилирован.
                    // Этот метод используется для создания запросов, где значения параметров уже включены в сам запрос.
                    epicIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (epicIdInfo != null) {
                        int epicId = getValueOfId(epicIdInfo);
                        response = getEpicById(epicId).toString();
                    } else {
                        response = getAllEpics().toString();
                    }
                    printResponse(response, httpExchange);
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), UTF_8);
                    Epic newEpic = gson.fromJson(jsonString, Epic.class);
                    if (!epics.containsKey(newEpic.getId())) {
                        addEpic(newEpic);
                        response = String.format("Эпик под id = %d успешно создан", newEpic.getId());
                    } else {
                        updateEpicById(newEpic.getId(), newEpic);
                        response = String.format("Эпик под id = %d успешно обновлен", newEpic.getId());
                    }
                    printResponse(response, httpExchange);
                    break;
                case "DELETE":
                    epicIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (epicIdInfo != null) {
                        int epicId = getValueOfId(epicIdInfo);
                        removeEpicById(epicId);
                        response = String.format("Эпик под id = %d удален", epicId);
                    }
                    printResponse(response, httpExchange);
                    break;
            }
        });
    }

    private void workingOnSubtasks() {
        server.createContext("/tasks/subtask", (httpExchange) -> { //для подзадач — /tasks/subtask
            String method = httpExchange.getRequestMethod();
            String response = null;
            String subtaskIdInfo = null;
            switch (method) {
                case "GET":
                    subtaskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (subtaskIdInfo != null) {
                        int subtaskId = getValueOfId(subtaskIdInfo);
                        response = getSubtaskById(subtaskId).toString();
                    }
                    // Так, а тут мы не можем получить список всех подзадач без id конкретного эпика...
                    printResponse(response, httpExchange);
                    break;
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes());
                    Subtask newSubtask = gson.fromJson(jsonString, Subtask.class);
                    Epic epic = getEpicById(newSubtask.getEpicId());
                    if (!subtasks.containsKey(newSubtask.getId())) {
                        addSubtask(newSubtask, epic); //Нужен эпик, к которому будет привязана эта подзадача...
                        //Эпик указывается при создании продзадачи, следовательно, можно вытащить его из newSubtask
                        System.out.println("Подзадача создана");
                        response = String.format("Подзадача под id = %d успешно создана", newSubtask.getId());
                    } else {
                        updateSubtask(newSubtask.getEpicId(), newSubtask);
                        response = String.format("Подзадача под id = %d успешно обновлена", newSubtask.getId());
                    }
                    break;
                case "DELETE":
                    subtaskIdInfo = httpExchange.getRequestURI().getRawQuery();
                    if (subtaskIdInfo != null) {
                        int subtaskId = getValueOfId(subtaskIdInfo);
                        removeSubtaskById(subtaskId);
                        response = String.format("Подзадача под id = %d удалена", subtaskId);
                    }
                    printResponse(response, httpExchange);
                    break;
            }
        });
    }

    /**
     * Получить все задачи сразу можно будет по пути /tasks/, а получить историю задач по пути /tasks/history.
     * Так же, пока еще не реализована возможность получения всех подзадач
     */

    //Получаем все подзадачи эпика. Так как выше, ввиду спецификации подзадач реализовать это возможности не было.
    private void workWithAllSubtasksOfEpic() {               //для подзадач — /tasks/subtask/epic
        server.createContext("/tasks/subtask/epic", (httpExchange) -> {
            String method = httpExchange.getRequestMethod();
            String response = null;
            String epicIdInfo = null;
            if (method.equals("GET")) {
                epicIdInfo = httpExchange.getRequestURI().getRawQuery();
                if (epicIdInfo != null) {
                    int epicId = getValueOfId(epicIdInfo);
                    response = getAllSubtasksByEpic(epicId).toString();
                }
                printResponse(response, httpExchange);
            }
        });
    }

    private void getAllTasks() {   //Получить все задачи сразу можно будет по пути /tasks/
        server.createContext("/tasks", (httpExchange) -> {
            String method = httpExchange.getRequestMethod();
            String response = null;
            if (method.equals("GET")) {
                response = getPrioritizedTasks().toString();
            } else {
                response = "Ошибка обработки запроса " + method;
            }
            printResponse(response, httpExchange);
        });
    }

    private void workingWithHistory() {      // получить историю задач по пути /tasks/history
        server.createContext("/tasks/history", httpExchange -> {
            String method = httpExchange.getRequestMethod();
            String response = null;
            if (method.equals("GET")) {
                response = historyList().toString();
            } else {
                response = "Ошибка обработки запроса " + method;
            }
            printResponse(response, httpExchange);
        });
    }

    private int getValueOfId(String infOfId) {          ////Вынесем в отдельный метод, так как используется в коде довольно часто
        String[] infOfIfSplit = infOfId.split("=");
        return Integer.parseInt(infOfIfSplit[1]);
    }

    private void printResponse(String response, HttpExchange httpExchange) { //Вынесем в отдельный метод, так как используется в коде довольно часто
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
