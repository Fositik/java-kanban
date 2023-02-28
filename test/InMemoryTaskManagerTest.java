package ru.yandex.practicum.model;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Для двух менеджеров задач InMemoryTasksManager и FileBackedTasksManager.
 * Чтобы избежать дублирования кода, необходим базовый класс с тестами на каждый метод из
 *   интерфейса abstract class TaskManagerTest<T extends TaskManager>.
 * Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
 * Для каждого метода нужно проверить его работу:
 *   a. Со стандартным поведением.
 *   b. С пустым списком задач.
 *   c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
 */
class InMemoryTaskManagerTest {

}