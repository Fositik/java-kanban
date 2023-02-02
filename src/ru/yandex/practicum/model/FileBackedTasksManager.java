package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;
import ru.yandex.practicum.model.TaskType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

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
        //Данная строка содержит полное название класса (включая пакеты) -> "ru.yandex.practicum.service.Epic"
        String fullClassName = task.getClass().getTypeName();
        /**Для того, чтобы получить строку, содержащую только тип задачи,
         * находим индекс крайней подстроки, которая содержит точку
         */
        String taskType = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String result;

        if (taskType.equals("Task") || taskType.equals("Epic")) {
            result = String.format("%s,%s,%s,%s,%s",
                    task.getId(),
                    taskType,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        } else {
            result = String.format("%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    taskType,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    ((Subtask) task).getEpic().getId()); //Используем явное приведение task к Subtask
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
        String taskType = taskValues[1];
        Task task = null;
        switch (taskType) {
            case "Task":
                task = new Task(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                /**
                 * Конструктор Task имеет следующий вид:
                 * public Task(String name, String description, Status status) {
                 *         this.name = name;
                 *         this.description = description;
                 *         this.status = status;
                 *     }
                 *     Поэтому задаем параметры именно в таком порядке
                 */
                task.setId(Integer.parseInt(taskValues[0])); //Парсим, так как на вход подается строка, а нам нужен int
                /**
                 * Устанваливаем Id отдельно, так как в конструкторе не предусмотрен такой параметр
                 */
            case "Epic":
                task = new Epic(taskValues[2], taskValues[4], Status.valueOf(taskValues[3]));
                task.setId(Integer.parseInt(taskValues[0]));
            case "Subtask":
                task = new Subtask(taskValues[2], taskValues[4], Status.valueOf(taskValues[0]));
                task.setId(Integer.parseInt(taskValues[0]));
                int epicId = Integer.parseInt(taskValues[5]);
                if (subtasks.containsKey(epicId)) {
                    ((Subtask) task).setEpic(getEpicById(epicId));
                    getEpicById(epicId).getSubtasks().add((Subtask) task);
                }
        }
        return task;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> browsingHistory = manager.getHistory();
        StringBuilder historyId = new StringBuilder();
        historyId.append("\n");
        for (Task task : browsingHistory) {
                historyId.append(String.format("%d,", task.getId()));
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
            throw new SaveException();
        }
    }

    //    static FileBackedTasksManager  loadFromFile(File file){
//
//    }
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("test.csv");
        //Задача id 1
        Task takeExams = new Task("Сдать Экзамены", "На отлично");
        fileBackedTasksManager.addSimpleTask(takeExams);
        Task goHome = new Task("go home");
        fileBackedTasksManager.addSimpleTask(goHome);
        fileBackedTasksManager.getSimpleTaskById(1);
        fileBackedTasksManager.getSimpleTaskById(2);

        Epic doTheLessons = new Epic("Сделать уроки","Описание");
        fileBackedTasksManager.addEpic(doTheLessons);
        Subtask biology = new Subtask("Биология","доклад", doTheLessons);
        fileBackedTasksManager.addSubtask(biology, doTheLessons);
        Subtask math = new Subtask("Математика","задачи", doTheLessons);
       fileBackedTasksManager.addSubtask(math, doTheLessons);
    }
}
