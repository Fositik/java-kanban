
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.InMemoryTaskManager;
import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.util.List;

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
    private final String startTime = "02.03.2023|10:00";
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;
    Task task;

    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    void createEpicAndSubtasks() {
        epic1 = new Epic("TestEpic");
        subtask1 = new Subtask("TestSubtask1");
        subtask2 = new Subtask("TestSubtask2");
    }

    void createTask() {
        task = new Task("TestTask");
        task.setStartTime(startTime);
        task.setDuration(15);
    }

    @Test
    void shouldReturnLocalDataTimeOfTask() {
        createTask();
        assertEquals("02.03.2023|10:00", task.getStartTime().format(Task.formatter));
        assertEquals(15, task.getDuration().toMinutes());
        assertEquals("02.03.2023|10:15", task.getEndTime().format(Task.formatter));
    }

    @Test
    void shouldReturnLocalDataTimeOfSubtask() {
        createEpicAndSubtasks();
        subtask1.setStartTime(startTime);
        subtask1.setDuration(15);
        assertEquals("02.03.2023|10:00", subtask1.getStartTime().format(Task.formatter));
        assertEquals(15, subtask1.getDuration().toMinutes());
        assertEquals("02.03.2023|10:15", subtask1.getEndTime().format(Task.formatter));
    }

    @Test
    void shouldReturnLocalDataTimeOfEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        createEpicAndSubtasks();
        subtask1.setStartTime(startTime);
        subtask1.setDuration(30);
        subtask2.setStartTime("02.03.2023|10:20");
        subtask2.setDuration(60);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        assertEquals("02.03.2023|10:00", epic1.getStartTime().format(Task.formatter));
        assertEquals(90, epic1.getDuration().toMinutes());
        assertEquals("02.03.2023|11:20", epic1.getEndTime().format(Task.formatter));
    }

    //Когда во второй подзадаче не определены дата старта и продолжительность
    @Test
    void shouldReturnLocalDateTimeWhenOnSecondSubtaskStartTimeAndDurationNotDefined() {
        TaskManager taskManager = new InMemoryTaskManager();
        createEpicAndSubtasks();
        subtask1.setStartTime(startTime);
        subtask1.setDuration(30);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        assertEquals("02.03.2023|10:00", epic1.getStartTime().format(Task.formatter));
        assertEquals(30, epic1.getDuration().toMinutes());
        assertEquals("02.03.2023|10:30", epic1.getEndTime().format(Task.formatter));
    }

    @Test
    void shouldReturnLocalDateTimeOfEpicWhoseSubtasksStartAtDifferentTimes() {
        TaskManager taskManager = new InMemoryTaskManager();
        createEpicAndSubtasks();
        subtask1.setStartTime(startTime);
        subtask1.setDuration(3600);
        subtask2.setStartTime("02.03.2024|10:00");
        subtask2.setDuration(360);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        assertEquals("02.03.2023|10:00", epic1.getStartTime().format(Task.formatter));
        assertEquals(3960, epic1.getDuration().toMinutes());
        assertEquals("02.03.2024|16:00", epic1.getEndTime().format(Task.formatter));
    }

    @Test
    void shouldReturnSortedListByTime() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("task1");
        task1.setStartTime("02.03.2023|10:00");
        Task task2 = new Task("task2");
        task2.setStartTime("02.03.2023|11:00");
        Task task3 = new Task("task3");
        // task3.setStartTime("02.03.2023|12:00");
        Task task4 = new Task("task4");
        task4.setStartTime("02.03.2023|13:00");
        taskManager.addSimpleTask(task1);
        taskManager.addSimpleTask(task2);
        taskManager.addSimpleTask(task3);
        taskManager.addSimpleTask(task4);
        Task[] expectedSortedTaskList = new Task[]{task1,task2,task4,task3};
        Task[] realSortedTaskList = ((InMemoryTaskManager)taskManager).getPrioritizedTasks().toArray(Task[]::new);
        assertArrayEquals(expectedSortedTaskList,realSortedTaskList);
    }
}