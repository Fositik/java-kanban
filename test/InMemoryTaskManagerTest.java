
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
 * Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
 * Для каждого метода нужно проверить его работу:
 * a. Со стандартным поведением.
 * b. С пустым списком задач.
 * c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager taskManager;
    private final String startTime = "28.02.2022|10:00";
    Epic epic;
    Subtask subtask;
    Task task;

    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }



}