package ru.yandex.practicum;
import java.util.Objects;

public class SimpleTask extends Task {
  protected String description;    //у простой задачи должно быть описание

    public SimpleTask(String name, int id, String description, Status status) {
        super(name, id, status);
        this.description = description;
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
        if (!super.equals(o)) return false;
        SimpleTask that = (SimpleTask) o;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                "description='" + description + '\'' +
                ", status=" + status +
                "}";
    }
}
