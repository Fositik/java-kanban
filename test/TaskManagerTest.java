import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод
 * из интерфейса abstract class TaskManagerTest<T extends TaskManager>.
 */
abstract public class TaskManagerTest<T extends TaskManager> {
    private final T object;
    TaskManager taskManager;
    Epic epic;
    Subtask subtask;
    Task task;

    public TaskManagerTest(T object) {
        this.object = object;
    }

    @BeforeEach
    void createTAskManager() {
        taskManager = object;
        epic = new Epic("Поесть");
        subtask = new Subtask("Заказать роллы");
        task = new Task("Выпить сок");
    }

    @Test
    void addSimpleTask() {
        taskManager.addSimpleTask(task);
        Task[] expectedTaskList = new Task[]{task};
        Task[] realTaskList = taskManager.getAllSimpleTasks().toArray(Task[]::new);
        assertArrayEquals(
                expectedTaskList,
                realTaskList,
                "Arrays do not match."
        );
    }

    @Test
    void addEpic() {
        taskManager.addEpic(epic);
        Epic[] expectedEpicList = new Epic[]{epic};
        Epic[] realEpicList = taskManager.getAllEpics().toArray(Epic[]::new);
        assertArrayEquals(
                expectedEpicList,
                realEpicList,
                "Arrays do not match."
        );

    }

    @Test
    void addSubtask() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        Subtask[] expectedSubtaskList = new Subtask[]{subtask};
        Subtask[] realSubtaskList = taskManager.getAllSubtasksByEpic(epic.getId()).toArray(Subtask[]::new);
        assertArrayEquals(
                expectedSubtaskList,
                realSubtaskList,
                "Arrays do not match."
        );
    }

    @Test
    void getSubtaskById() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);


    }

    @Test
    void getEpicById() {
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getSimpleTaskById() {
    }

    @Test
    void getAllSimpleTasks() {
    }

    @Test
    void getAllEpics() {
    }

    @Test
    void getAllSubtasksByEpic() {
    }

    @Test
    void removeSimbletaskById() {
    }

    @Test
    void removeEpicById() {
    }

    @Test
    void removeSubtaskById() {
    }

    @Test
    void updateSimpleTaskById() {
    }

    @Test
    void updateEpicById() {
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void removeAllSimpleTasks() {
    }

    @Test
    void removeAllSubtasks() {
    }

    @Test
    void removeAllEpics() {
    }
}