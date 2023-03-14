import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Для расчёта статуса Epic. Граничные условия:
 * a.   Пустой список подзадач.             |++
 * b.   Все подзадачи со статусом NEW.      |++
 * c.    Все подзадачи со статусом DONE.    |++
 * d.    Подзадачи со статусами NEW и DONE. |++
 * e.    Подзадачи со статусом IN_PROGRESS. |++
 */
class EpicTest {
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach //Выполняется перед каждым тестом
    private void beforeEach() {  //Создаем эпик и подзадачи
        epic = new Epic("Добится успеха");
        subtask1 = new Subtask("Выполнить все планы");
        subtask2 = new Subtask("Вопрлотить все свои желания");
    }

    @Test
    public void theListOfSubtasksMustBeEmpty() {  //Список подзадач пуст
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void allSubtasksMustHaveTheStatusNEW() { //Все подзадачи со статусом NEW.
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        subtask1.setEpic(epic);
        subtask2.setEpic(epic);
        //Статусы подзадач NEW по дефолту
        Assertions.assertEquals(Status.NEW, epic.getStatus()); //проверка статуса эпика на NEW +
    }

    @Test
    public void allSubtasksMustHaveTheStatusDONE() { //Все подзадачи со статусом DONE.
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        subtask1.setEpic(epic);
        subtask2.setEpic(epic);
        //Устанавливаем статусы DONE подзадачам
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic.checkEpicStatusDone();
        assertEquals(Status.DONE, epic.getStatus()); //проверка статуса эпика на DONE +
    }

    @Test
    public void subtasksWithStatusDoneAndNew() {    //Подзадачи со статусами NEW и DONE.
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        subtask1.setEpic(epic);
        subtask2.setEpic(epic);
        //Устанавливаем статусы DONE & NEW подзадачам
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.NEW);
        //Обновляем статус эпика
        epic.checkEpicStatusDone();
        epic.checkEpicStatusInProgresss();
        assertEquals(Status.IN_PROGRESS, epic.getStatus()); //проверка статуса эпика на IN_PROGRESS +
    }

    @Test
    public void allSubtasksMustHaveTheStatusIN_PROGRESS() { //Подзадачи со статусом IN_PROGRESS.
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);
        subtask1.setEpic(epic);
        subtask2.setEpic(epic);
        //Устанавливаем статусы IN_PROGRESS подзадачам
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        epic.checkEpicStatusInProgresss();
        assertEquals(Status.IN_PROGRESS, epic.getStatus());  //проверка статуса эпика на IN_PROGRESS +
    }

}