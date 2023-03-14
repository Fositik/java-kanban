package ru.yandex.practicum.model;

import com.google.gson.*;
import ru.yandex.practicum.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.client.KVTaskClient;
import ru.yandex.practicum.http.gson.deserialize.DurationJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.EpicJsonDeserializer;
import ru.yandex.practicum.http.gson.deserialize.SubtaskJsonDeserializer;
import ru.yandex.practicum.http.gson.serialize.DurationJsonSerializer;
import ru.yandex.practicum.http.gson.serialize.EpicJsonSerializer;
import ru.yandex.practicum.http.gson.serialize.SubtaskJsonSerializer;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;

import java.time.LocalDateTime;


//Теперь можно создать новую реализацию интерфейса TaskManager — класс HttpTaskManager.
// Он будет наследовать от FileBackedTasksManager.
//Конструктор HttpTaskManager должен будет вместо имени файла принимать URL к серверу KVServer
public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(String path) throws URISyntaxException, IOException, InterruptedException {
        super(path);
        this.kvTaskClient = new KVTaskClient(path);
    }

    @Override
    public void save() {
        for (Task task : simpleTasks.values()) {
            //Метод getJsonString в Java используется для получения строкового представления объекта в формате JSON.
            // Он возвращает строку, которая содержит данные объекта в формате JSON.
            String json = getJsonString(task);
            kvTaskClient.put(String.valueOf(task.getId()), json);
        }

        for (Subtask subtask : subtasks.values()) {
            String json = getJsonString(subtask);
            kvTaskClient.put(String.valueOf(subtask.getId()), json);
        }
        for (Epic epic : epics.values()) {
            String json = getJsonString(epic);
            kvTaskClient.put(String.valueOf(epic.getId()), json);
        }
    }

    public Task load(String key) {
        String json = kvTaskClient.load(key);
        if (json != null) {
            return readJsonString(json, getSimpleNameOfTask(Integer.parseInt(key)));
        } else return null;
    }

    private Task readJsonString(String json, String sinpleName) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, new SubtaskJsonDeserializer())
                .registerTypeAdapter(Epic.class, new EpicJsonDeserializer())
                .registerTypeAdapter(Duration.class, new DurationJsonDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        if (epics.equals("Epic")) {
            Epic epic = gson.fromJson(json, Epic.class);
            linkSubtasksToEpic(epic);
            return epic;
        } else if (sinpleName.equals("Subtask")) {
            Subtask subtask = gson.fromJson(json, Subtask.class);
            linkEpicToSubtask(subtask, subtask.getEpicId());
            return subtask;
        } else
            return gson.fromJson(json, Task.class);
    }

    private String getJsonString(Task task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, new SubtaskJsonSerializer())
                .registerTypeAdapter(Epic.class, new EpicJsonSerializer())
                .registerTypeAdapter(Duration.class, new DurationJsonSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        if (task instanceof Subtask) {
            return gson.toJson(task, Subtask.class);
        } else if (task instanceof Epic) {
            return gson.toJson(task, Epic.class);
        } else
            return gson.toJson(task, Task.class);
    }

    private String getSimpleNameOfTask(int taskID) {
        if (simpleTasks.containsKey(taskID)) {
            return simpleTasks.get(taskID).getClass().getSimpleName();
        } else if (subtasks.containsKey(taskID)) {
            return subtasks.get(taskID).getClass().getSimpleName();
        } else
            return epics.get(taskID).getClass().getSimpleName();
    }
}

