package ru.yandex.practicum.service;

import java.util.Objects;

public class Task {             //общими чертами для всех задач являются:
    protected String name;      //имя самой задачи
    protected int id;           //уникальный идентификатор, по которому можно найти и управлять задачей
    protected String description;
    protected Status status;    //текущий статус задачи

    //Решил протестировать перегрузку и понял, что указывать id при создании новой задачи нет никакой необходимости.
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    //Перегрузка конструктора. Когда передаем имя и описание. Статус по умолчанию равен NEW
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    //Когда передаем только имя. Описание по умолчанию равно null, а сатаус - NEW
    public Task(String name) {
        this.name = name;
        this.description = null;
        this.status = Status.NEW;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && name.equals(task.name) && description.equals(task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }


}
