package ru.yandex.practicum.service;

import java.util.Objects;

public class Subtask extends Task {
    //переменная для хранения идентификатора эпика
    private int epicId;
    private Epic epic;

    public Subtask(int id, String name, String description, Status status, int epicId, Epic epic) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.epic = epic;
    }

    //геттер для id эпика
    public int getEpicId() {
        return epicId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

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
        return epicId == subtask.epicId && description.equals(subtask.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                "description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
