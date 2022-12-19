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

    public void getAllTasks() {
        getAllSimpleTasks();
        getAllEpics();
    }


    public ArrayList<SimpleTask> getAllSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Integer> getAllSubtask(int epicId) {
        return new ArrayList<Integer>(epics.get(epicId).getSubtasksIds());
    }

    public void updateSubtask(int subtaskId) {

    }


    public void deletingAllTasks() {
        simpleTasks.clear();
        epics.clear();
        subtasks.clear();
    }

}
