package ru.yandex.practicum;

import ru.yandex.practicum.state.TaskStatus;

public class Task {
    String name;
    String description;
    int id;
    TaskStatus status;

    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }
}
