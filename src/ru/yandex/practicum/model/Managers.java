package ru.yandex.practicum.model;

/*Утилитарный класс. На нём лежит вся ответственность за создание менеджера задач.
Managers сам подбирает нужную реализацию TaskManager и возвращать объект правильного типа.
 */
public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
