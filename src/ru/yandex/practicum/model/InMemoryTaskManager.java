package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.util.ArrayList;
import java.util.HashMap;

//В класс InMemoryTaskManager были перенесены все методы из TaskManager
public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();
    //Счетчик для идентификаторов задач
    private int nextId = 1;
    //хеш-таблица для хранения списка просых задач
    protected HashMap<Integer, Task> simpleTasks = new HashMap<>();
    //хеш-таблица для хранения списка эпиков
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    //хеш-таблица для хранения списка подзадач
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    @Override
    //метод для добавления простой задачи
    public int addSimpleTask(Task task) {
        task.setId(nextId++);
        simpleTasks.put(task.getId(), task);
        System.out.println("Задача успешно создана! ID = " + task.getId());
        return task.getId();
    }

    @Override
    //метод для добавления эпика
    public int addEpic(Epic epic) {
        //задаем уникальный id эпику
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        System.out.println("Эпик успешно создан! ID = " + epic.getId());
        return epic.getId();
    }

    @Override
    //метод для добавления подзадачи
    public int addSubtask(Subtask subtask, Epic epic) {
        //задаем уникальный id подзадаче
        subtask.setId(nextId++);
        //если epicId подзадачи совпадает с существующ
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с id " + subtask.getId() + " уже существует!");
        } else {
            //добавляем подзадачу в список подзадач
            subtasks.put(subtask.getId(), subtask);
            //Добавляем подзадачу в список SubttasksIds для того, чтобы иметь возможность получать список подзадач эпика
            epics.get(epic.getId()).getSubtasks().add(subtask);

            System.out.println("Подзадача к эпику id = " + subtask.getId() + " успешно создана!");
        }
        return subtask.getId();
    }

    @Override
    //метод для получения подзадачи по id
    public Subtask getSubtaskById(int subtaskId) {
        //метод для добавления подзадачи в список истории просмотра
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    //метод для получения эпика по id
    public Epic getEpicById(int epicId) {
        //метод для добавления эпика в список истории просмотра
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    //метод для получения задачи по id
    public Task getSimpleTaskById(int simpleTaskId) {
        //метод для добавления задачи в список истории просмотра
        historyManager.add(simpleTasks.get(simpleTaskId));
        return simpleTasks.get(simpleTaskId);
    }

    @Override
    //метод для получения списка всех задач
    public ArrayList<Task> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    //метод для получения списка всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    //метод для получения id подзадач эпика
    public ArrayList<Subtask> getAllSubtasksByEpic(int epicId) {
        return new ArrayList<>(epics.get(epicId).getSubtasks());
    }

    @Override
    //метод для удаления задачи по id
    public void removeSimbletaskById(int simpleTaskId) {
        simpleTasks.remove(simpleTaskId);
        System.out.println("Задача под id= " + simpleTaskId + " была успешно удалена!");
    }

    @Override
    //метод для удаления эпика по id-->вместе с эпиковм удаляются все подзадачи
    public void removeEpicById(int epicId) {
        epics.get(epicId).getSubtasks().clear();
        epics.remove(epicId);
        System.out.println("Эпик под id= " + epicId + " и все его подзадачи успешно удалены!");
    }

    @Override
    //метод для удаления подзадачи по id
    public void removeSubtaskById(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    @Override
    //метод для обновления задачи по id
    public Task updateSimpleTaskById(int taskId, Task task) {
        if (simpleTasks.containsKey(taskId)) {
            Task value = simpleTasks.get(taskId);
            value.setName(task.getName());
            value.setStatus(task.getStatus());
            value.setDescription(task.getDescription());
            return value;
        } else {
            System.out.println("Задачи под id = " + taskId + " не существует!");
            return null;
        }
    }

    @Override
    //метод для обновления задачи по id
    public Epic updateEpicById(int taskId, Epic epic) {
        if (epics.containsKey(taskId)) {
            //id остается прежним
            Epic value = epics.get(taskId);
            value.setName(epic.getName());
            value.setStatus(epic.getStatus());
            return value;
        } else {
            System.out.println("Эпика под id = " + taskId + " не существует!");
            return null;
        }
    }

    @Override
    //метод для обновления подзадачи по id
    public Subtask updateSubtask(int taskId, Subtask subtask) {
        //id подзадачи остается прежним
        if (subtasks.containsKey(taskId)) {
            //id эпика тоже остается прежним
            Subtask value = subtasks.get(taskId);
            value.getEpic().equals(subtask.getEpic());
            value.setDescription(subtask.getDescription());
            value.setName(subtask.getName());
            value.setStatus(subtask.getStatus());
            epics.get(value.getEpic().getId()).checkEpicStatusDone();
            if (!epics.get(value.getEpic().getId()).getStatus().equals(Status.IN_PROGRESS)) {
                epics.get(value.getEpic().getId()).checkEpicStatusInProgresss();
            }
            return value;
        } else {
            System.out.println("Подзадачи под id = " + taskId + " не существует!");
            return null;
        }
    }

    @Override
    //методs очищают все хеш-таблицы, то есть, удалют все задачи/эпики/подзадачи
    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
    }
}
