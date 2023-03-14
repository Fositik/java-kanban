import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.InMemoryHistoryManager;
import ru.yandex.practicum.service.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    /**<T extends HistoryManager>*/ //отбросим
    /**
     * Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
     * a. Пустая история задач. +
     * b. Дублирование. +
     * с. Удаление из истории: начало, середина, конец.
     */
    private InMemoryHistoryManager inMemoryHistoryManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void createTasks() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        task1 = new Task("T1");
        task1.setId(1);
        task2 = new Task("T1");
        task2.setId(2);
        task3 = new Task("T1");
        task3.setId(3);
    }

    @Test
    void shouldReturnEmptyHistoryList() {
        assertNull(inMemoryHistoryManager.getHistory());
    }

    @Test
    void shouldReturnHistoryListWithSizeOne_WhenAddDuplicationOfTasks() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task1);
        assertEquals(1, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromTheTopOfTheHistoryList() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task1.getId());
        Task[] expectedHistoryList = new Task[]{task2, task3};
        assertArrayEquals(expectedHistoryList, inMemoryHistoryManager.getHistory().toArray(Task[]::new));
    }

    @Test
    void shouldRemoveTaskFromTheEndOfTheHistoryList() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task3.getId());
        Task[] expectedHistoryList = new Task[]{task1, task2};
        assertArrayEquals(expectedHistoryList, inMemoryHistoryManager.getHistory().toArray(Task[]::new));
    }

    @Test
    void shouldRemoveTaskFromTheMidOfTheHistoryList() {
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.remove(task2.getId());
        Task[] expectedHistoryList = new Task[]{task1, task3};
        assertArrayEquals(expectedHistoryList, inMemoryHistoryManager.getHistory().toArray(Task[]::new));
    }
}