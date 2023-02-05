package ru.yandex.practicum.model;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String START_STRING = "id,type,name,status,description,epic\n";
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
        super.getSubtaskById(subtaskId);
        save();
        return subtasks.get(subtaskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        super.getEpicById(epicId);
        save();
        return epics.get(epicId);
    }

    @Override
    public Task getSimpleTaskById(int taskId) {
        super.getSimpleTaskById(taskId);
        save();
        return simpleTasks.get(taskId);
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
        save();
        super.removeSubtaskById(subtaskId);
    }

    @Override
    public void removeSimbletaskById(int simpleTaskId) {
        save();
        super.removeSimbletaskById(simpleTaskId);
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
        if (taskType.equals("Task") || taskType.equals("Epic")) {   //Если сокращенное название equals "Task" or "Epic"
            result = String.format("%s,%s,%s,%s,%s",
                    task.getId(),
                    taskType.toUpperCase(Locale.ROOT),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            result = String.format("%s,%s,%s,%s,%s,%s",         //since subtasks have one more parameter - "epicid",
                    task.getId(),                               //we need an alternative condition for their formatting
                    taskType.toUpperCase(Locale.ROOT),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
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
 * taskValue[5] -> epic
 */
        String type = taskValues[1].toLowerCase(Locale.ROOT);
        String taskType = type.substring(0, 1).toUpperCase(Locale.ROOT) + type.substring(1);
        Task task = null;
        switch (taskType) {             //Switch, I think the best solution in this situation
            case "Task":
                task = new Task(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0])); //Парсим, так как на вход подается строка, а нам нужен int
                // Устанваливаем Id отдельно, так как в конструкторе не предусмотрен такой параметр
                break;
            case "Epic":
                task = new Epic(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0]));
                break;
            case "Subtask":
                task = new Subtask(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0]));
                int epicId = Integer.parseInt(taskValues[5]);
                if (subtasks.containsKey(epicId)) {
                    ((Subtask) task).setEpic(getEpicById(epicId));
                    getEpicById(epicId).getSubtasks().add((Subtask) task);
                }
                break;
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
            System.err.println(String.format("java-kanban\\%s",path));
            e.getStackTrace();

        }
    }

    public static @NotNull FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        HistoryManager historyManager = new InMemoryHistoryManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isBlank()) {
                    if (!line.equals("id,type,name,status,description,epic")) {
                        Task task = fileBackedTasksManager.fromString(line);
                        switch (task.getClass().getSimpleName()) {
                            case "Task":
                                fileBackedTasksManager.simpleTasks.put(task.getId(), task);
                                break;
                            case "Epic":
                                fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                                break;
                            case "Subtask":
                                fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);
                                break;
                        }
                    }
                } else {
                    String newLine = reader.readLine();
                    List<Integer> historyId = historyFromString(newLine);
                    Task task = null;
                    for (Integer id : historyId) {
                        if (fileBackedTasksManager.simpleTasks.containsKey(id)) {
                            task = fileBackedTasksManager.simpleTasks.get(id);
                            historyManager.add(task);
                        } else if (fileBackedTasksManager.epics.containsKey(id)) {
                            task = fileBackedTasksManager.epics.get(id);
                            historyManager.add(task);
                        } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                            task = fileBackedTasksManager.subtasks.get(id);
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
        return fileBackedTasksManager;
    }

    public static void main(String[] args) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("test.csv");
        //Задача id 1
        Task takeExams = new Task("Сдать Экзамены", "На отлично");
        fileBackedTasksManager.addSimpleTask(takeExams);
        //Задача id 2
        Task goHome = new Task("go home");
        fileBackedTasksManager.addSimpleTask(goHome);
        fileBackedTasksManager.getSimpleTaskById(1);
        fileBackedTasksManager.getSimpleTaskById(2);
        //Задача id 3
        Epic doTheLessons = new Epic("Сделать уроки", "Описание");
        fileBackedTasksManager.addEpic(doTheLessons);
        //Задача id 4
        Subtask biology = new Subtask("Биология", "доклад", doTheLessons);
        fileBackedTasksManager.addSubtask(biology, doTheLessons);
        //Задача id 5
        Subtask math = new Subtask("Математика", "задачи", doTheLessons);
        fileBackedTasksManager.addSubtask(math, doTheLessons);
        fileBackedTasksManager.updateSubtask(4, new Subtask("Bio", "Deskription", Status.DONE));
        loadFromFile("tst.csv");
        System.out.println(fileBackedTasksManager.getAllEpics());
        System.out.println(fileBackedTasksManager.getAllSimpleTasks());
        System.out.println(historyManager.getHistory());
    }
}
