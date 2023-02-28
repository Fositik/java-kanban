package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.util.*;
import java.util.stream.Collectors;

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
    //TreeSet для хранения отсортированного списка задач по времени
    protected TreeSet<Task> sortedTasks;

    @Override
    //метод для добавления простой задачи
    public void addSimpleTask(Task task) {
        task.setId(nextId++);
        simpleTasks.put(task.getId(), task);
        isNotIntersection(task);
        System.out.println("Задача успешно создана! ID = " + task.getId());
    }

    @Override
    //метод для добавления эпика
    public void addEpic(Epic epic) {
        //задаем уникальный id эпику
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        System.out.println("Эпик успешно создан! ID = " + epic.getId());
    }

    @Override
    //метод для добавления подзадачи
    public void addSubtask(Subtask subtask, Epic epic) {
        //задаем уникальный id подзадаче
        subtask.setId(nextId++);
        //если epicId подзадача совпадает с существующей
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с id " + subtask.getId() + " уже существует!");
        } else {
            //добавляем подзадачу в список подзадач
            subtasks.put(subtask.getId(), subtask);
            subtask.setEpic(epic);
            isNotIntersection(subtask);
            //Добавляем подзадачу в список SubtasksIds для того, чтобы иметь возможность получать список подзадач эпика
            epics.get(epic.getId()).getSubtasks().add(subtask);
            epics.get(epic.getId()).calculateStatrTimeAndDuration();
            System.out.println("Подзадача к эпику id = " + subtask.getId() + " успешно создана!");
        }
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
        historyManager.remove(simpleTaskId);
        System.out.println("Задача под id= " + simpleTaskId + " была успешно удалена!");
    }

    @Override
    //метод для удаления эпика по id-->вместе с эпиковм удаляются все подзадачи
    public void removeEpicById(int epicId) {
        historyManager.remove(epicId);
        for (Subtask subtask : epics.get(epicId).getSubtasks()) {
            historyManager.remove(subtask.getId());
            // subtasks.remove(subtask.getId());
        }
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
            isNotIntersection(task);
            value.setStartTime(task.getStartTime());
            value.setDuration(task.getDuration());
            System.out.printf("Задача под id = "+taskId+" успешно обновлена!");
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
            Subtask value = subtasks.get(taskId);               //id
            value.getEpic().equals(subtask.getEpic());          //
            value.setDescription(subtask.getDescription());
            value.setName(subtask.getName());
            value.setStatus(subtask.getStatus());
            isNotIntersection(subtask);
            value.setStartTime(subtask.getStartTime());
            value.setDuration(subtask.getDuration());
            //Вызываем метод для проверки статуса эпика на DONE
            epics.get(value.getEpic().getId()).checkEpicStatusDone();
            //Если статус эпика не IN_PROGRESS, то вызываем метод для проверки на IN_PROGRESS
            if (!epics.get(value.getEpic().getId()).getStatus().equals(Status.IN_PROGRESS)) {
                epics.get(value.getEpic().getId()).checkEpicStatusInProgresss();
            }
            System.out.printf("Подзадача под id = "+taskId+" успешно обновлена!");
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

    /**
     * Отсортируйте все задачи по приоритету — то есть по startTime.
     * Если дата старта не задана, добавьте задачу в конец списка задач, подзадач, отсортированных по startTime.
     * Напишите новый метод getPrioritizedTasks, возвращающий список задач и подзадач в заданном порядке.
     *
     * @return
     */
    //То есть, для начала нам необходимо получить список задач отсортированных по времени (1) - метод getAllTasksWithStartTime
    //Далее, получить список задач, не отсортированных по времени (2) - метод getAllTasksWithoutStartTime
    //Для этого необходимо получить список всех имебщихся задач (3) - метод getAllTasks
    //Далее, написать метод getPrioritizedTasks, который будет сортировать задачи методом compareTo, а так де добавлять список отсортированных по времени задач в начало списка, а не отсортированных - в конец
    private ArrayList<Task> getAllTasks() {             //Метод, который возвращает список всех задач
        ArrayList<Task> allTasks = new ArrayList<>();   //Эпики в этот список не входят, тк их продолжительность/время старта/время окончания формируется исходя из соответствующих параметров подзадач
        allTasks.addAll(simpleTasks.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    private List<Task> getAllTasksWithStartTime() {                 //Получаем список задач со временем старта
        ArrayList<Task> allTasksWithStartTime = new ArrayList<>();  //Список, который мы будем возвращать
        ArrayList<Task> allTasks = getAllTasks();
        for (Task task : allTasks) {                                //Перебираем список всех задач
            if (task.getStartTime() != null) {                      //Если время старта не null
                allTasksWithStartTime.add(task);                    //Добавляем в наш список
            }
        }
        return allTasksWithStartTime;
    }

    private List<Task> getAllTasksWithoutStartTime() {
        ArrayList<Task> allTasksWithoutStartTime = new ArrayList<>();
        ArrayList<Task> allTasks = getAllTasks();
        for (Task task : allTasks) {
            if (task.getStartTime() == null) {
                allTasksWithoutStartTime.add(task);
            }
        }
        return allTasksWithoutStartTime;
    }

    public List<Task> getPrioritizedTasks() {
        if (sortedTasks != null && sortedTasks.size() == getAllTasks().size()) {
            List<Task> allTask = new ArrayList<>(sortedTasks);
            allTask.addAll(getAllTasksWithoutStartTime());
            return allTask;
        } else {
            List<Task> allTasks = getAllTasksWithStartTime().stream().sorted(Comparator.comparing(Task::getStartTime)).collect(Collectors.toList());
            sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
            sortedTasks.addAll(allTasks);
            allTasks.addAll(getAllTasksWithoutStartTime());
            return allTasks;
        }
    }

    /**
     * Подсказка: как искать пересечения за O(1)
     * Пусть все задачи располагаются на сетке с интервалами в 15 минут, а планирование возможно только на год вперёд.
     * В этом случае можно заранее заполнить таблицу, где ключ — это интервал, а значение — объект boolean (свободно время или нет).
     * В итоге для эффективного поиска пересечений достаточно будет проверить, что свободны все 15-минутные интервалы задачи.
     */
    private boolean isNotIntersection(Task task) {
        for (Task priorityTask : getPrioritizedTasks()) {
            /**
             * Вылетает NullPointerException...разбираемся.
             */
            //1 - Циклом проходим по задачам с getPrioritizedTasks()
            //2 - нужен boolean - флаг (?)
            //3 -
            if (
                    task.getStartTime().isAfter(priorityTask.getStartTime())
                    && task.getStartTime().isBefore(priorityTask.getEndTime())) {
                return false;
            }
            if (
                   task.getEndTime().isAfter(priorityTask.getStartTime())
                    && task.getEndTime().isBefore(priorityTask.getEndTime())) {
                return true;
            }
        }
        return true;
    }
}
