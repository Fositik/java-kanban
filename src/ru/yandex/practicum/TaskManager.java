package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    protected int nextId = 1;    //Счетчик для идентификаторов задач
    protected HashMap<Integer, SimpleTask> simpleTasks = new HashMap<>();   //хеш-таблица для хранения списка просых задач
    protected HashMap<Integer, Epic> epics = new HashMap<>();               //хеш-таблица для хранения списка эпиков
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();         //хеш-таблица для хранения списка подзадач

    public int addSimpleTask(SimpleTask simpleTask) {                       //метод для добавления простой задачи
        simpleTask.setId(nextId++);
        simpleTasks.put(simpleTask.getId(), simpleTask);
        System.out.println("Задача успешно создана! ID = " + simpleTask.getId());
        return simpleTask.getId();
    }

    public int addEpic(Epic epic) {      //метод для добавления эпика
        epic.setId(nextId++);            //задаем уникальный id эпику
        epics.put(epic.getId(), epic);
        System.out.println("Эпик успешно создан! ID = " + epic.getId());
        return epic.getId();
    }

    public int addSubtask(Subtask subtask, Epic epic) {   //метод для добавления подзадачи
        subtask.setId(nextId++);                          //задаем уникальный id подзадаче
        subtasks.put(subtask.getId(), subtask);           //добавляем подзадачу в список подзадач
        if (subtask.getEpicId() == epic.getId()) {        //если epicId подзадачи совпадает с существующим эпиком
            epic.addSubtask(subtask.getId());             //привязываем подзадачу к эпику
        } else {
            System.out.println("Эпика под id = " + subtask.getEpicId() + " не существует!");
        }
        sincEpic(subtask.getEpicId());
        System.out.println("Подзадача к эпику id = " + subtask.getEpicId() + " успешно создана!");
        return subtask.getId();
    }

    private void sincEpic(int epicId) {             //метод проверяет статусы подзадач
        Epic epic = epics.get(epicId);              //и синхронихирует статусы эпика с и подзадач
        checkEpicStatusInProgresss(epicId);
        checkEpicStatusDone(epicId);
        epic.getSubtasksIds().size();
    }

    public void checkEpicStatusInProgresss(int epicId) {    //проверяем статусы подзадач на IN_PROGRESS
        Epic epic = epics.get(epicId);
        for (Integer subtasksId : epic.getSubtasksIds()) {
            if (subtasks.get(subtasksId).getStatus().equals(Status.IN_PROGRESS)) {  //если хотя бы одна задача имеет статус IN_PROGRESS
                epics.get(epicId).setStatus(Status.IN_PROGRESS);                    //то и весь эпик получает такой статус
            }
        }
    }

    public void checkEpicStatusDone(int epicId) {           //проверяем статусы подзадач на DONE
        boolean checkStatus = true;                         //булевая переменная для проверки статуса эпика на DONE
        Epic epic = epics.get(epicId);
        for (Integer subtasksId : epic.getSubtasksIds()) {                      //пробегаемся по всем подзадачам
            if (!subtasks.get(subtasksId).getStatus().equals(Status.DONE)) {    //если подзадча имеет статус отличный от DONE
                checkStatus = false;                                            //то статус эпика не DONE
            }
            break;
        }
        if (checkStatus) {             //если же, все задачи имеют статус DONE
            epics.get(epicId).setStatus(Status.DONE);           //то статус эпика тоже считается таким же
            System.out.println("Эпик под id = " + epics.get(epicId) + "выполнен!");
        }
    }

    public Subtask getSubtaskById(int subtaskId) {               //метод для получения подзадачи по id
        return subtasks.get(subtaskId);
    }

    public Epic getEpicById(int epicId) {                        //метод для получения эпика по id
        return epics.get(epicId);
    }

    public SimpleTask getSimpleTaskById(int simpleTaskId) {      //метод для получения задачи по id
        return simpleTasks.get(simpleTaskId);
    }

    public ArrayList<SimpleTask> getAllSimpleTasks() {           //метод для получения списка всех задач
        return new ArrayList<>(simpleTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {                       //метод для получения списка всех эпиков
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Integer> getAllSubtasksByEpic(int epicId) { //метод для получения id подзадач эпика
        return new ArrayList<>(epics.get(epicId).getSubtasksIds());
    }

    public void removeSimbletaskById(int simpleTaskId) { //метод для удаления задачи по id
        simpleTasks.remove(simpleTaskId);
        System.out.println("Задача под id= " + simpleTaskId + " была успешно удалена!");
    }

    public void removeEpicById(int epicId) {  //метод для удаления эпика по id-->вместе с эпиковм удаляются все подзадачи
        epics.get(epicId).getSubtasksIds().clear();
        epics.remove(epicId);
        System.out.println("Эпик под id= " + epicId + " и все его подзадачи успешно удалены!");
    }

    public void removeSubtaskById(int subtaskId) { //метод для удаления подзадачи по id
        subtasks.remove(subtaskId);
    }

    public SimpleTask updateSimpleTaskById(int taskId, SimpleTask simpleTask) {
        if (simpleTasks.containsKey(taskId)) {
            SimpleTask value = simpleTasks.get(taskId);
            value.setName(simpleTask.getName());
            value.setStatus(simpleTask.getStatus());
            value.setDescription(simpleTask.getDescription());
            return value;
        } else {
            System.out.println("Задачи под id = " + taskId + " не существует!");
            return null;
        }
    }

    public Epic updateSimpleTaskById(int taskId, Epic epic) {
        if (epics.containsKey(taskId)) {
            Epic value = epics.get(taskId);
            value.setName(epic.getName());
            value.setStatus(epic.getStatus());
            return value;
        } else {
            System.out.println("Эпика под id = " + taskId + " не существует!");
            return null;
        }
    }

    public void updateSubtask(int taskId, Subtask subtask) {
        if (subtasks.containsKey(taskId)) {
    Subtask value =subtasks.get(taskId);
    
        }
    }

    public void removeAllTasks() {
        simpleTasks.clear();
        epics.clear();
        subtasks.clear();
    }

}
