package ru.yandex.practicum.model;

import java.io.IOException;
import java.net.URISyntaxException;

/*Утилитарный класс. На нём лежит вся ответственность за создание менеджера задач.
Managers сам подбирает нужную реализацию TaskManager и возвращать объект правильного типа.
 */
public class Managers {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HttpTaskManager getDefault(String uri) {
        try {
            return new HttpTaskManager(uri);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.getMessage();
        }
        return null;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public TaskManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }
}
