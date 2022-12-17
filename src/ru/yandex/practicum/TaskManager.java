package ru.yandex.practicum;


import java.util.HashMap;

public class TaskManager {

    protected int nextId = 1;    //Счетчик для идентификаторов задач
    protected HashMap<Integer, SimpleTask> simpleTasks = new HashMap<>();   //хеш-таблица для хранения списка просых задач
    protected HashMap<Integer, Epic> epics = new HashMap<>();               //хеш-таблица для хранения списка эпиков
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();         //хеш-таблица для хранения списка подзадач

    public int addSimpleTask(SimpleTask simpleTask) {                       //метод для добавления простой задачи
        simpleTask.setId(nextId++);
        simpleTasks.put(simpleTask.getId(), simpleTask);
        return simpleTask.getId();
    }

    public int addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    private void sincEpic(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subtasksId : epic.getSubtasksIds()) {
            subtasks.get(subtasksId);
            if (subtasks.get(subtasksId).equals(Status.IN_PROGRESS)) {
                epics.get(epicId).setStatus(Status.IN_PROGRESS);
            }
        }
        epic.getSubtasksIds().size();
    }

    public void checkEpicStatusDone(int epicId) {
        boolean checkStatus = true;
        Epic epic = epics.get(epicId);
        for (Integer subtasksId : epic.getSubtasksIds()) {
            if (!subtasks.get(subtasksId).equals(Status.DONE)) {
            }
            checkStatus = false;
            break;
        }
        if (checkStatus) {
            epics.get(epicId).setStatus(Status.DONE);
            System.out.println("Эпик под id = " + epics.get(epicId) + "выполнен!");
        }
    }

    public int addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        sincEpic(subtask.getEpicId());
        return subtask.getId();
    }

    public void updateSubtask(Subtask subtask) {

    }

    public void updateEpic(Epic epic) {

    }

    public void deletingAllTasks() {
        simpleTasks.clear();
        epics.clear();
        subtasks.clear();
    }

}
