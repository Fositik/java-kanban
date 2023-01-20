package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.util.ArrayList;

//Метод таск менеджер переделали в интерфейс
public interface TaskManager {
    //Метод для добавления простой задачи
    int addSimpleTask(Task task);

    //Метод для добавления эпика
    int addEpic(Epic epic);

    //Метод для добавления подзадачи
    int addSubtask(Subtask subtask, Epic epic);

    //Метод для нахождения подзадачи по id
    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    Task getSimpleTaskById(int simpleTaskId);

    //Метод для получения списка просых задач
    ArrayList<Task> getAllSimpleTasks();

    //Метод для получения списка эпиков
    ArrayList<Epic> getAllEpics();

    //Метод для получения списка подзадач
    ArrayList<Subtask> getAllSubtasksByEpic(int epicId);

    //Метод для удаления простой задачи по id
    void removeSimbletaskById(int simpleTaskId);

    //Метод для удаления эпика по id
    void removeEpicById(int epicId);

    //Метод для удаления подзадачи по id
    void removeSubtaskById(int subtaskId);

    //Метод для обновления задачи по по id
    Task updateSimpleTaskById(int taskId, Task task);

    //Метод для обновления эпика по по id
    Epic updateEpicById(int taskId, Epic epic);

    //Метод для обновления задачи по по id
    Subtask updateSubtask(int taskId, Subtask subtask);

    void removeAllSimpleTasks();

    void removeAllSubtasks();

    void removeAllEpics();
}
