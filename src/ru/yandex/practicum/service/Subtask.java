package ru.yandex.practicum.service;

import java.util.Objects;

public class Subtask extends Task {

   /* private int epicId;  //переменная больше не нужна
   Так как раньше мы передавали в качестве параметра и epicId и сам класс Epic, что было излишним
   Было решено оставить только Epic
   Соответственно, код был переработан
    */
    private Epic epic;

    public Subtask(int id, String name, String description, Status status,  Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                "description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
