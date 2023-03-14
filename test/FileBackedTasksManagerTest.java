import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.FileBackedTasksManager;
import ru.yandex.practicum.model.Managers;
import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//На этот тест-класс убил 2 дня. Так как ты правишь одну ошибку, а получешь - две. :C
//Но зато, я уверен, что работа с файлами теперь идет без багов

/**
 * Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния.
 * Граничные условия:
 * a. Пустой список задач.
 * b. Эпик без подзадач.
 * c. Пустой список истории.
 */
public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final String path = "test.csv";
    private final Managers managers = new Managers();
    Task testTask1;
    Task testTask2;
    Task testTask3;
    Epic epicTest;
    Subtask subtaskTest1;
    Subtask subtaskTest2;

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager("test.csv"));
    }

    @BeforeEach
    void initTasks() {
        testTask1 = new Task("T1", "D1", LocalDateTime.now(), Duration.ofMinutes(15));
        testTask2 = new Task("T2", "D2", LocalDateTime.now().plus(Duration.ofMinutes(100)), Duration.ofMinutes(14));
        testTask3 = new Task("T3", "D3", LocalDateTime.now().plus(Duration.ofMinutes(200)), Duration.ofMinutes(16));
        epicTest = new Epic("E1", "D1");
        subtaskTest1 = new Subtask("S1", "D1", LocalDateTime.now().plus(Duration.ofMinutes(300)), Duration.ofMinutes(45), epicTest);
        subtaskTest2 = new Subtask("S2", "D2", LocalDateTime.now().plus(Duration.ofMinutes(400)), Duration.ofMinutes(23), epicTest);
    }

    @AfterEach
    public void deleteFile() {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldCorrectlySaveAndLoadFromFile() {
        TaskManager taskManager = managers.getFileBackedTasksManager(path);
        taskManager.addSimpleTask(testTask1);
        taskManager.addEpic(epicTest);
        ((FileBackedTasksManager) taskManager).loadFromFile(path);
        assertEquals(List.of(testTask1), taskManager.getAllSimpleTasks());
        assertEquals(List.of(epicTest), taskManager.getAllEpics());
    }

    @Test
    void shouldDownloadFromFileWithEmptyTaskList() {            //Пустой список задач
        TaskManager firstManager = managers.getFileBackedTasksManager(path);
        firstManager.addSimpleTask(testTask1);
        firstManager.removeSimbletaskById(testTask1.getId());
        TaskManager secondManager = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) secondManager).loadFromFile(path);
        assertTrue(secondManager.getAllSimpleTasks().isEmpty());
    }

    @Test
    void shouldLoadFromFileWithTaskList() {
        //УБИЛ НА ЭТОТ ТЕСТ ПАРУ ДНЕЙ 	(ಥ﹏ಥ) -> косяк был в методе loadFromFile
        //FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        TaskManager firstManager = managers.getFileBackedTasksManager(path);
        firstManager.addSimpleTask(testTask1);
        TaskManager secondManager = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) secondManager).loadFromFile(path);
        assertEquals(
                testTask1.getName(),
                secondManager.getSimpleTaskById(testTask1.getId()).getName()
        );
        assertEquals(
                "D1",
                secondManager.getSimpleTaskById(testTask1.getId()).getDescription()
        );
        assertEquals(
                testTask1.getStatus(),
                secondManager.getSimpleTaskById(testTask1.getId()).getStatus()
        );
        assertEquals(
                testTask1.getDuration(),
                secondManager.getSimpleTaskById(testTask1.getId()).getDuration()
        );
    }

    @Test
    void shouldDownloadFromFileWithEpicWithoutSubtasks() {      //Если эпик без подзадач
        //Тоже пришлось повозиться в поисках ошибки. -> метод fromString
        TaskManager taskManager1 = managers.getFileBackedTasksManager(path);
        taskManager1.addEpic(epicTest);
        // taskManager1.addSimpleTask(testTask1);
        TaskManager taskManager2 = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) taskManager2).loadFromFile(path);
        assertTrue(taskManager2.getAllSubtasksByEpic(epicTest.getId()).isEmpty());
    }

    @Test
    void shouldLoadFromFileEpicWithSubtasks() {     //Эпик с подзадачами
        //Боже, и тут косяк был (．．;) -> всё тот же метод fromString
        TaskManager taskManager1 = managers.getFileBackedTasksManager(path);
        taskManager1.addEpic(epicTest);
        taskManager1.addSubtask(subtaskTest1, epicTest);
        taskManager1.addSubtask(subtaskTest2, epicTest);
        taskManager1.addSubtask(subtaskTest2, epicTest); //Не должен засчитаться, так как это повтор предыдущего эпика
        TaskManager taskManager2 = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) taskManager2).loadFromFile(path);
        assertEquals(2, taskManager2.getAllSubtasksByEpic(epicTest.getId()).size());
    }

    @Test
    void shouldDownloadFromFileWithEmptyHistoryList() {
        TaskManager firstManager = managers.getFileBackedTasksManager(path);
        firstManager.addSimpleTask(testTask1);
        TaskManager secondManager = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) secondManager).loadFromFile(path);
        assertNull(((FileBackedTasksManager) secondManager).historyList());
    }

    @Test
    void shouldDownloadFromFileWithHistoryList() {
        TaskManager firstManager = managers.getFileBackedTasksManager(path);
        firstManager.addSimpleTask(testTask1);
        firstManager.addSimpleTask(testTask2);
        firstManager.addSimpleTask(testTask3);
        firstManager.addEpic(epicTest);
        firstManager.addSubtask(subtaskTest1, epicTest);
        firstManager.addSubtask(subtaskTest2, epicTest);

        firstManager.getSimpleTaskById(testTask1.getId());
        firstManager.getSimpleTaskById(testTask2.getId());
        firstManager.getSimpleTaskById(testTask3.getId());
        firstManager.getEpicById(epicTest.getId());
        firstManager.getSubtaskById(subtaskTest1.getId());
        firstManager.getSubtaskById(subtaskTest2.getId());

        System.out.println(firstManager.historyList());

        TaskManager secondManager = managers.getFileBackedTasksManager(path);
        ((FileBackedTasksManager) secondManager).loadFromFile(path);
        List<Task> hist = secondManager.historyList();
        System.out.println("Список истории со второге менеджера: " + hist);
        //   assertNotNull( secondManager.historyList());
        assertEquals(5, hist.size());
    }
}