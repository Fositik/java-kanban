package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.http.gson.serialize.DurationJsonSerializer;
import ru.yandex.practicum.http.gson.serialize.EpicJsonSerializer;
import ru.yandex.practicum.http.gson.serialize.SubtaskJsonSerializer;
import ru.yandex.practicum.http.server.KVServer;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        KVServer kvServer;
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Subtask.class, new SubtaskJsonSerializer())
                    .registerTypeAdapter(Epic.class, new EpicJsonSerializer())
                    .registerTypeAdapter(Duration.class, new DurationJsonSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
            kvServer = new KVServer();
            kvServer.start();
            //  HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault("http://localhost:" + KVServer.PORT);

            Task takeExams = new Task("Сдать Экзамены", LocalDateTime.now(), Duration.ofMinutes(30));
            httpTaskManager.addSimpleTask(takeExams);

            //Задача id 2
            Task pickUpTheChild = new Task("Заехать в школу", LocalDateTime.now().plus(Duration.ofMinutes(21)), Duration.ofMinutes(30));
            httpTaskManager.addSimpleTask(pickUpTheChild);

            //Эпик id 3
            Epic buyProducts = new Epic("Купить продукты");
            httpTaskManager.addEpic(buyProducts);

            //Подзадача id 4, эпик id 3
            Subtask milk = new Subtask("Молоко", buyProducts);
            httpTaskManager.addSubtask(milk, buyProducts);

            //Подзадача id 5, эпик id 3
            Subtask bread = new Subtask("Хлеб", LocalDateTime.now().plus(Duration.ofMinutes(111)), Duration.ofMinutes(30), buyProducts);
            httpTaskManager.addSubtask(bread, buyProducts);

            //Подзадача id 6, эпик id 3
            Subtask butter = new Subtask("Масло", LocalDateTime.now().plus(Duration.ofMinutes(153)), Duration.ofMinutes(30), buyProducts);
            httpTaskManager.addSubtask(butter, buyProducts);

            //Эпик id 7
            Epic doTheLessons = new Epic("Сделать уроки");
            httpTaskManager.addEpic(doTheLessons);

            //Подзадача id 8, эпик id 6
            Subtask biology = new Subtask("Биология", LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
            httpTaskManager.addSubtask(biology, doTheLessons);

            //Подзадача id 9, эпик id 6
            Subtask math = new Subtask("Математика", LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
            httpTaskManager.addSubtask(math, doTheLessons);
            Subtask adf = new Subtask("Математика2", LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
            httpTaskManager.addSubtask(adf, doTheLessons);

            Task takeExams2 = new Task("Сдать Экзамены");
            httpTaskManager.addSimpleTask(takeExams2);

            httpTaskManager.getSimpleTaskById(pickUpTheChild.getId());
            httpTaskManager.getSubtaskById(bread.getId());
            httpTaskManager.getSimpleTaskById(takeExams.getId());
            httpTaskManager.getEpicById(buyProducts.getId());
            httpTaskManager.getEpicById(doTheLessons.getId());
            httpTaskManager.getSubtaskById(math.getId());

            System.out.println("All simple tasks" + gson.toJson(httpTaskManager.getAllSimpleTasks()));
            System.out.println("All epics" + gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("All subtasks" + gson.toJson(httpTaskManager.getAllSubtasksByEpic(doTheLessons.getId())));
            System.out.println(httpTaskManager);
            kvServer.stop();

        } catch (IOException e) {
            e.getMessage();
        }

    }
}