package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String START_STRING = "id,type,name,status,description,startTime,duration,epic\n";
    protected final String path;

    public FileBackedTasksManager(String path) {
        this.path = path;
    }

    @Override
    public void addSimpleTask(Task task) {
        super.addSimpleTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, Epic epic) {
        super.addSubtask(subtask, epic);
        save();
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    public Epic getEpic(int epicId){
        return epics.get(epicId);
    }

    @Override
    public Task getSimpleTaskById(int taskId) {
        Task task = super.getSimpleTaskById(taskId);
        save();
        return task;
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        save();
        return super.getAllEpics();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeSimbletaskById(int simpleTaskId) {
        super.removeSimbletaskById(simpleTaskId);
        save();
    }

    @Override
    public Task updateSimpleTaskById(int taskId, Task task) {
        Task updatedSimpleTask = super.updateSimpleTaskById(taskId, task);
        save();
        return updatedSimpleTask;
    }

    @Override
    public Epic updateEpicById(int taskId, Epic epic) {
        Epic updatedEpic = super.updateEpicById(taskId, epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(int taskId, Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(taskId, subtask);
        save();
        return updatedSubtask;
    }

    /**
     * "id,type,name,status,description,epic"
     *
     * @param task
     * @return
     */
    private String toString(Task task) {
        String taskType = task.getClass().getSimpleName();          //Сокращенное название класса (не включая пакеты)
        String result;                                              //"ru.yandex.practicum.service.Epic" -> "Epic"
        String startTime;
        long durationInMinutes;
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(Task.formatter);
        } else {
            startTime = null;
        }
        if (task.getDuration() != null) {
            durationInMinutes = task.getDuration().toMinutes();
        } else {
            durationInMinutes = 0;
        }
        if (taskType.equals("Task") || taskType.equals("Epic")) {   //Если сокращенное название equals "Task" or "Epic"
            result = String.format("%s,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    taskType.toUpperCase(Locale.ROOT),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    startTime,
                    durationInMinutes);
        } else {
            result = String.format("%s,%s,%s,%s,%s,%s,%s,%s",         //since subtasks have one more parameter - "epicid",
                    task.getId(),                               //we need an alternative condition for their formatting
                    taskType.toUpperCase(Locale.ROOT),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    startTime,
                    durationInMinutes,
                    ((Subtask) task).getEpic().getId());        //Используем явное приведение task к Subtask
        }
        return result;
    }

    private Task fromString(String value) {
        String[] taskValues = value.split(",");
/**
 * taskValue[0] -> id
 * taskValue[1] -> type
 * taskValue[2] -> name
 * taskValue[3] -> status
 * taskValue[4] -> description
 * taskValue[5] -> startTime
 * taskValue[6] -> duration
 * taskValue[7] -> epic
 */
        String type = taskValues[1].toLowerCase(Locale.ROOT);
        String taskType = type.substring(0, 1).toUpperCase(Locale.ROOT) + type.substring(1);
        Task task = null;
        switch (taskType) {             //Switch, I think the best solution in this situation
            case "Task":
                task = new Task(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0])); //Парсим, так как на вход подается строка, а нам нужен int
                // Устанваливаем Id отдельно, так как в конструкторе не предусмотрен такой параметр
              //  historyManager.add(simpleTasks.get(task.getId()));
               // historyManager.add(task);
                break;
            case "Epic":
                task = new Epic(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0]));
              //  historyManager.add(simpleTasks.get(task.getId()));
              //  historyManager.add(task);
                break;
            case "Subtask":
                task = new Subtask(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0]));
                int epicId = Integer.parseInt(taskValues[7]);
              //  historyManager.add(simpleTasks.get(task.getId()));
               // historyManager.add(task);
                if (epics.containsKey(epicId)) {
                    ((Subtask) task).setEpic(getEpic(epicId));
                    getEpic(epicId).getSubtasks().add((Subtask) task);
                }
                break;
        }
        if (task != null) {
            if (!taskValues[5].equals("null")) {
                task.setStartTime(taskValues[5]);
            }
//             else {
//                task.setStartTime(task.getStartTime());
//            }
            if (!taskValues[6].equals("0")) {
                task.setDuration(Long.parseLong(taskValues[6]));
            }
        }
        return task;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> browsingHistory = manager.getHistory();
        StringBuilder historyId = new StringBuilder();
        historyId.append("\n");
        //Цикл fori, чтобы определить, является элемент последним (после последнего элемента запятая не нужна)
        for (int i = 0; i < browsingHistory.size(); i++) {
            if (i == browsingHistory.size() - 1) {
                historyId.append(String.format("%d", browsingHistory.get(i).getId()));
            } else {
                historyId.append(String.format("%d,", browsingHistory.get(i).getId()));
            }
        }
        return historyId.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        String[] browsingHistoryId = value.split(",");
        for (String s : browsingHistoryId) {
            historyId.add(Integer.parseInt(s));
        }
        return historyId;
    }


    protected void save() {
        try (FileWriter fileWriter = new FileWriter(path, UTF_8)) {
            fileWriter.write(START_STRING);
            for (Task simpleTask : simpleTasks.values()) {
                fileWriter.write(toString(simpleTask) + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask) + "\n");
            }
            if (historyManager.getHistory() != null) {
                fileWriter.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            System.err.println(String.format("java-kanban\\%s", path));
            e.getStackTrace();

        }
    }

    public void loadFromFile(String path) {
     //   FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isBlank()) {
                    if (!line.equals("id,type,name,status,description,startTime,duration,epic")) {
                        Task task = fromString(line);
                        switch (task.getClass().getSimpleName()) {
                            case "Task": //-> simpleTasks.put(task.getId(), task);
                                simpleTasks.put(task.getId(), task);
                                break;
                            case "Epic"://->epics.put(task.getId(), (Epic) task);
                                epics.put(task.getId(), (Epic) task);
                                break;
                            case "Subtask": //-> subtasks.put(task.getId(), (Subtask) task);
                                subtasks.put(task.getId(), (Subtask) task);
                                break;
                        }
                    }
                } else {
                    String newLine = reader.readLine(); //Пропуск строки
                    List<Integer> historyId = historyFromString(newLine);
                    Task task = null;
                    for (Integer id : historyId) {
                        if (simpleTasks.containsKey(id)) {
                            task = simpleTasks.get(id);
                            historyManager.add(task);
                        } else if (epics.containsKey(id)) {
                            task = epics.get(id);
                            historyManager.add(task);
                        } else if (subtasks.containsKey(id)) {
                            task = subtasks.get(id);
                            historyManager.add(task);
                        }
                    }
                    if (task != null) {
                        historyManager.add(task);

                    }
                }
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        //  return fileBackedTasksManager;
    }

    public static void main(String[] args) {
//        HistoryManager historyManager = Managers.getDefaultHistory();
//        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("test.csv");
//        //Задача id 1
//        Task takeExams = new Task("Сдать Экзамены", "На отлично",LocalDateTime.now(), Duration.ofMinutes(15));
//        fileBackedTasksManager.addSimpleTask(takeExams);
//        //Задача id 2
//        Task goHome = new Task("go home",LocalDateTime.now().plus(Duration.ofMinutes(55)), Duration.ofMinutes(15));
//        fileBackedTasksManager.addSimpleTask(goHome);
//        fileBackedTasksManager.getSimpleTaskById(1);
//        fileBackedTasksManager.getSimpleTaskById(2);
//        //Задача id 3
//        Epic doTheLessons = new Epic("Сделать уроки", "Описание");
//        fileBackedTasksManager.addEpic(doTheLessons);
//        //Задача id 4
//        Subtask biology = new Subtask(
//                "Биология",
//                "доклад",
//                LocalDateTime.now().plus(Duration.ofMinutes(123)),
//                        Duration.ofMinutes(15),
//                        doTheLessons);
//        fileBackedTasksManager.addSubtask(biology, doTheLessons);
//        //Задача id 5
//        Subtask math = new Subtask("Математика", "задачи",LocalDateTime.now().plus(Duration.ofMinutes(35)), Duration.ofMinutes(15), doTheLessons);
//        fileBackedTasksManager.addSubtask(math, doTheLessons);
//        fileBackedTasksManager.updateSubtask(4, new Subtask("Bio", "Description", Status.DONE));
//        fileBackedTasksManager.loadFromFile(fileBackedTasksManager.path);
//        System.out.println("Задачи по приоритету"+fileBackedTasksManager.getPrioritizedTasks().toString());
//        System.out.println(fileBackedTasksManager.getAllEpics());
//        System.out.println(fileBackedTasksManager.getAllSimpleTasks());
//        System.out.println(historyManager.getHistory());
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("FileTest.csv");
//        fileBackedTasksManager.loadFromFile(fileBackedTasksManager.path);
//        fileBackedTasksManager.getSimpleTaskById()
        final String path = "test.csv";
        final Managers managers = new Managers();
        TaskManager firstManager = managers.getFileBackedTasksManager("FileTest.csv");
        Epic epicTest = new Epic("E1", "D1");
        ((FileBackedTasksManager) firstManager).addEpic(epicTest);
        Subtask subtask1 = new Subtask("S1", "D1", LocalDateTime.now(), Duration.ofMinutes(45), epicTest);
        ((FileBackedTasksManager) firstManager).addSubtask(subtask1, epicTest);
        Subtask subtask2 = new Subtask("S2", "D2", LocalDateTime.now(), Duration.ofMinutes(34), epicTest);
        ((FileBackedTasksManager) firstManager).addSubtask(subtask2, epicTest);
        Task task1 = new Task("T1", "D1", LocalDateTime.now(), Duration.ofMinutes(43));
        ((FileBackedTasksManager) firstManager).addSimpleTask(task1);

        firstManager.getSimpleTaskById(task1.getId());
        firstManager.getEpicById(epicTest.getId());
        firstManager.getSubtaskById(subtask1.getId());
        firstManager.getSubtaskById(subtask2.getId());

       TaskManager secondManager = managers.getFileBackedTasksManager("FileTest.csv");
       ((FileBackedTasksManager) secondManager).loadFromFile("FileTest.csv");
        System.out.println(secondManager.getAllSimpleTasks());
        System.out.println(secondManager.getAllEpics());
        System.out.println(secondManager.getAllSubtasksByEpic(epicTest.getId()));

    //    System.out.println(secondManager.historyList());

//        System.out.println(secondManager.getAllEpics());
//        System.out.println(secondManager.getAllSimpleTasks());


    }
}
