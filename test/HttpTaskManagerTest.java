import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.HttpTaskManager;
import ru.yandex.practicum.http.server.KVServer;
import ru.yandex.practicum.model.Managers;
import ru.yandex.practicum.model.TaskManager;
import ru.yandex.practicum.service.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            manager = Managers.getDefault("http://localhost:" + KVServer.PORT);
        } catch (IOException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1)
        );
        Task task2 = new Task(
                "name2",
                "description2",
                Status.NEW,
                LocalDateTime.now().plus(Duration.ofMinutes(5)),
                Duration.ofMinutes(1)
        );
        manager.addSimpleTask(task1);
        manager.addSimpleTask(task2);
        manager.getSimpleTaskById(task1.getId());
        manager.getSimpleTaskById(task2.getId());
        List<Task> historyList = manager.historyList();
        assertEquals(manager.getAllSimpleTasks(), historyList);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic(
                "name1",
                "description1",
                Status.NEW
        );
        Epic epic2 = new Epic(
                "name2",
                "description2",
                Status.NEW
        );

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> historyList = manager.historyList();
        assertEquals(manager.getAllEpics(), historyList);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic1 = new Epic(
                "name1",
                "description1",
                Status.NEW
        );
        Subtask subtask1 = new Subtask(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(1),
                epic1
        );
        Subtask subtask2 = new Subtask(
                "name1",
                "description1",
                Status.NEW,
                LocalDateTime.now().plus(Duration.ofMinutes(12)),
                Duration.ofMinutes(1),
                epic1
        );
        manager.addEpic(epic1);
        manager.addSubtask(subtask1, epic1);
        manager.addSubtask(subtask2, epic1);
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        List<Task> list = manager.historyList();
        assertEquals(manager.getAllSubtasksByEpic(epic1.getId()), list);
    }
}
