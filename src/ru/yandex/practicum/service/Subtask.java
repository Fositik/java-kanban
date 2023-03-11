package ru.yandex.practicum.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private int epicId;  //Возвращаем переменную

    private Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    /**
     * Так как для чтения подзадач из CSV файла было необходимо указать epicId, а нужного конструктора у меня не было,
     * пришлось создать еще один конструктор без параметра Task
     */
    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Subtask(String name, Epic epic) {
        super(name);
        this.epic = epic;
    }

    public Subtask(String name, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, startTime, duration);
        this.epic = epic;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, startTime, duration);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
    }


    public Subtask(String name) {
        super(name);
    }

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epic.equals(subtask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                " description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
