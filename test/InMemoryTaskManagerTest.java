
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.InMemoryTaskManager;
import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Для двух менеджеров задач InMemoryTasksManager и FileBackedTasksManager.
 * Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод из
 * интерфейса abstract class TaskManagerTest<T extends TaskManager>.
 * <p>
 * Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
 * <p>
 * Для каждого метода нужно проверить его работу:
 * a. Со стандартным поведением.
 * b. С пустым списком задач.
 * c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager taskManager;
    private final String startTime = "28.02.2022|10:00";
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Task task;

    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    void createEpicAndSubtasks() {
        epic = new Epic("TestEpic");
        subtask1 = new Subtask("TestSubtask1");
        subtask2 = new Subtask("TestSubtask2");
    }

    void createTask() {
        task = new Task("TestTask");
        task.setStartTime(startTime);
        task.setDuration(15);
    }

    void shouldReturnLocalDataTimeOfTask() {
        createTask();

    }
}