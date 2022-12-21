package ru.yandex.practicum;
import java.util.Objects;

public class Subtask extends Task {
    protected String description;   //у подзадач есть описание
    protected int epicId;           //переменная для хранения идентификатора эпика

    public Subtask(String name, int id, String description, int epicId, Status status) {   //конструктор для подзадач
        super(name, id, status);
        this.description = description;
        this.epicId = epicId;
    }

    public int getEpicId(){     //геттер для id эпика
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
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                "description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
