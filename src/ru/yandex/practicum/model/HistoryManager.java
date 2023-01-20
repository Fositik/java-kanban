package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
