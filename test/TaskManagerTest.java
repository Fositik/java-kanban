import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод
 * из интерфейса abstract class TaskManagerTest<T extends TaskManager>.
 */
abstract public class TaskManagerTest<T extends TaskManager> {
    protected final T object;
    TaskManager taskManager;
    Epic epic;
    Subtask subtask;
    Task task;


    public TaskManagerTest(T object) {
        this.object = object;
    }

    @BeforeEach
    void createTaskManager() {
        taskManager = object;
        epic = new Epic("Поесть");
        subtask = new Subtask(
                "Заказать пиццы",
                LocalDateTime.now().plus(Duration.ofMinutes(120)),
                Duration.ofMinutes(15),
                epic
        );
        task = new Task(
                "Выпить сок",
                LocalDateTime.now(),
                Duration.ofMinutes(15)
        );
    }

    @Test
    void shouldCreateSimpleTask() {
        object.addSimpleTask(task);
        Task[] expectedTaskList = new Task[]{task};
        Task[] realTaskList = taskManager.getAllSimpleTasks().toArray(Task[]::new);
        assertArrayEquals(
                expectedTaskList,
                realTaskList,
                "Arrays are not equal"
        );
    }

    @Test
    void shouldDontCreateNewSimpleTask() {
        taskManager.addSimpleTask(task);
        taskManager.addSimpleTask(task);
        int expectedSize = 1;
        assertEquals(
                expectedSize,
                taskManager.getAllSimpleTasks().size(),
                "Created a copy of the simple task"
        );  //Изначально тест был провален
    }


    @Test
    void shouldCreateEpic() {
        taskManager.addEpic(epic);
        Epic[] expectedEpicList = new Epic[]{epic};
        Epic[] realEpicList = taskManager.getAllEpics().toArray(Epic[]::new);
        assertArrayEquals(
                expectedEpicList,
                realEpicList,
                "Arrays are not equal"
        );
    }

    @Test
    void shouldDontCreateNewEpic() {
        taskManager.addEpic(epic);
        taskManager.addEpic(epic);
        int expectedSize = 1;
        assertEquals(
                expectedSize,
                taskManager.getAllEpics().size(),
                "Created a copy of the epic");
    }

    @Test
    void shouldCreateSubtask() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        Subtask[] expectedSubtaskList = new Subtask[]{subtask};
        Subtask[] realSubtaskList = taskManager.getAllSubtasksByEpic(epic.getId()).toArray(Subtask[]::new);
        assertArrayEquals(
                expectedSubtaskList,
                realSubtaskList,
                "Arrays are not equal"
        );
    }

    @Test
    void shouldDontCreateNewSubtask() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        taskManager.addSubtask(subtask, epic);
        int expectedSize = 1;
        assertEquals(
                expectedSize,
                taskManager.getAllSubtasksByEpic(epic.getId()).size(),
                "Created a copy of the subtask"
        ); //Изначально тест был провален
    }

    @Test
    void shouldFindSubtaskById() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        assertEquals(
                subtask,
                taskManager.getSubtaskById(subtask.getId()),
                "Subtask not found."
        );
    }

    @Test
    void shouldFindEpicById() {
        taskManager.addEpic(epic);
        assertEquals(
                epic,
                taskManager.getEpicById(epic.getId()),
                "Epic not found."
        );
    }

    @Test
    void shouldFindSimpleTaskById() {
        taskManager.addSimpleTask(task);
        assertEquals(
                task,
                taskManager.getSimpleTaskById(task.getId()),
                "Simple task not found."
        );
    }

    @Test
    void shouldGetAllSimpleTasks() {
        taskManager.addSimpleTask(task);
        Task[] expectedTaskList = new Task[]{task};
        Task[] realTaskList = taskManager.getAllSimpleTasks().toArray(Task[]::new);
        assertArrayEquals(
                expectedTaskList,
                realTaskList,
                "Arrays are not equal!"
        );
    }

    @Test
    void shouldGetAllEpics() {
        taskManager.addEpic(epic);
        Epic[] expectedEpicList = new Epic[]{epic};
        Epic[] realEpicList = taskManager.getAllEpics().toArray(Epic[]::new);
        assertArrayEquals(
                expectedEpicList,
                realEpicList,
                "Arrays are not equal!"
        );
    }

    @Test
    void shouldGetAllSubtasksByEpic() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        Subtask[] expectedSubtaskList = new Subtask[]{subtask};
        Subtask[] realSubtaskList = taskManager.getAllSubtasksByEpic(epic.getId()).toArray(Subtask[]::new);
        assertArrayEquals(
                expectedSubtaskList,
                realSubtaskList,
                "Arrays are not equal!"
        );
    }

    @Test
    void shouldRemoveSimbletaskById() {
        taskManager.addSimpleTask(task);
        taskManager.removeSimbletaskById(task.getId());
        assertTrue(
                taskManager.getAllSimpleTasks().isEmpty(),
                "The task was not deleted");
    }

    @Test
    void shouldRemoveEpicById() {
        taskManager.addEpic(epic);
        taskManager.removeEpicById(epic.getId());
        assertTrue(
                taskManager.getAllEpics().isEmpty(),
                "The epic was not deleted"
        );
    }

    @Test
    void shouldRemoveSubtaskById() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        taskManager.removeSubtaskById(subtask.getId());
        assertTrue(
                epic.getSubtasks().isEmpty(),
                "The subtask was not deleted"
        );
        assertNull(
                taskManager.updateSubtask(subtask.getId(), new Subtask("Test")),
                "The subtask was not deleted"
        );
    }

    @Test
    void shouldUpdateSimpleTaskById() {             //Изначально крашилось, пришлось корректировать equals в классе Task
        taskManager.addSimpleTask(task);
        Task expectedSimpleTask = new Task("Купить новый ноутбук");
        expectedSimpleTask.setId(task.getId());
        assertEquals(
                expectedSimpleTask,
                taskManager.updateSimpleTaskById(task.getId(), new Task("Купить новый ноутбук")),
                "Tasks are different."
        );
    }

    @Test
    void shouldUpdateEpicById() {       //Изначально крашилось, пришлось корректировать equals в классе Epic
        taskManager.addEpic(epic);
        Epic expectedEpic = new Epic("Купить продукты");
        expectedEpic.setId(epic.getId());
        assertEquals(
                expectedEpic,
                taskManager.updateEpicById(epic.getId(), new Epic("Купить продукты")),
                "Epics are different."
        );
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        Subtask expectedSubtask = new Subtask("Молоко", epic);
        expectedSubtask.setId(subtask.getId());
        assertEquals(
                expectedSubtask,
                taskManager.updateSubtask(subtask.getId(), new Subtask("Молоко")),
                "Subtasks are different."
        );
    }

    @Test
    void shouldRemoveAllSimpleTasks() {
        taskManager.addSimpleTask(task);
        taskManager.removeAllSimpleTasks();
        assertTrue(
                taskManager.getAllSimpleTasks().isEmpty(),
                "Tasks have not been deleted"
        );
    }

    @Test
    void shouldRemoveAllSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask, epic);
        taskManager.removeAllSubtasks();
        assertTrue(
                taskManager.getAllSubtasksByEpic(epic.getId()).isEmpty(),
                "Subtasks have not been deleted"
        );
    }

    @Test
    void shouldRemoveAllEpics() {
        taskManager.addEpic(epic);
        taskManager.removeAllEpics();
        assertTrue(
                taskManager.getAllEpics().isEmpty(),
                "Epics have not been deleted"
        );

    }
}