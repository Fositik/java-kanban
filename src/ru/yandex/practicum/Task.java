package ru.yandex.practicum;

import java.util.Objects;

import static ru.yandex.practicum.Status.NEW;

public class Task {             //общими чертами для всех задач являются:
    protected String name;      //имя самой задачи
    protected int id;           //уникальный идентификатор, по которому можно найти и управлять задачей
    protected Status status;    //текущий статус задачи


    public Task(String name, int id, Status status) {
        this.name = name;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
