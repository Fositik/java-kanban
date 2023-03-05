
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
  // InMemoryTaskManager taskManager1;
    private TaskManager taskManager1 = new InMemoryTaskManager();
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

    //Проверка, что время старта и окончания эпика рассчитываются корректно
    @Test
    void shouldReturnLocalDataTimeOfEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        createEpicAndSubtasks();
        subtask1.setStartTime(startTime);
        subtask1.setDuration(30);
        subtask2.setStartTime("02.03.2023|10:31");
        subtask2.setDuration(60);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic1);
        assertEquals("02.03.2023|10:00", epic1.getStartTime().format(Task.formatter));
        assertEquals(90, epic1.getDuration().toMinutes());
        assertEquals("02.03.2023|11:31", epic1.getEndTime().format(Task.formatter));
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

    //Проверка времени старта и окончания эпика, когда подзадачи начинаются в разное время
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

    //Проверка на корректность сортировки задач по времени
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

    //Проверка пересечений, когда время старта задачи совпадает с предыдущей
    @Test
    void shouldCheckIntersection_IfTheStartDatesOfTheTasksAreTheSame(){
//        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("task1");
        task1.setStartTime("02.03.2023|10:00");
        task1.setDuration(30);
        Task task2 = new Task("task2");
        task2.setStartTime("02.03.2023|11:00");
        task2.setDuration(30);
        //Пересечение времени старта с task2
        Task task3 = new Task("task3");
        task3.setStartTime("02.03.2023|11:00");
        task3.setDuration(30);
        Task task4 = new Task("task4");
        task4.setStartTime("02.03.2023|13:00");
        task4.setDuration(30);
        taskManager1.addSimpleTask(task1);
        taskManager1.addSimpleTask(task2);
        taskManager1.addSimpleTask(task3);
        taskManager1.addSimpleTask(task4);

        Task[] expectedSortedTaskList = new Task[]{task1,task2,task4,task3};
       // Task[] realSortedTaskList = ((InMemoryTaskManager)taskManager).getPrioritizedTasks().toArray(Task[]::new);
        List<Task> realSortedTaskList =  ((InMemoryTaskManager)taskManager1).getPrioritizedTasks();
        assertArrayEquals(expectedSortedTaskList,realSortedTaskList.toArray(Task[]::new));
        assertNull(realSortedTaskList.get(realSortedTaskList.size()-1).getStartTime());
        assertNull(realSortedTaskList.get(realSortedTaskList.size()-1).getDuration());
    }

// Проверка пересечений, когда задача начинается во время выполнения предыдущей
    @Test
    void shouldCheckIntersrection_WhenNewTaskStartsAtTimeWhenThePreviousHasNotYetFinished(){
        Task task1 = new Task("task1");
        task1.setStartTime("02.03.2023|10:00");
        task1.setDuration(30);
        Task task2 = new Task("task2");
        task2.setStartTime("02.03.2023|11:00");
        task2.setDuration(30);
        //Пересечение времени старта с task2
        Task task3 = new Task("task3");
        task3.setStartTime("02.03.2023|10:30");
        task3.setDuration(30);
        Task task4 = new Task("task4");
        task4.setStartTime("02.03.2023|13:00");
        task4.setDuration(30);
        taskManager1.addSimpleTask(task1);
        taskManager1.addSimpleTask(task2);
        taskManager1.addSimpleTask(task3);
        taskManager1.addSimpleTask(task4);
        Task[] expectedSortedTaskList = new Task[]{task1,task2,task4,task3};
        List<Task> realSortedTaskList =  ((InMemoryTaskManager)taskManager1).getPrioritizedTasks();
        assertArrayEquals(expectedSortedTaskList,realSortedTaskList.toArray(Task[]::new));
        assertNull(realSortedTaskList.get(realSortedTaskList.size()-1).getStartTime());
        assertNull(realSortedTaskList.get(realSortedTaskList.size()-1).getDuration());
    }
}