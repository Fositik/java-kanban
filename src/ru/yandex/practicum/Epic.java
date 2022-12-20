package ru.yandex.practicum;


import java.util.ArrayList;
import java.util.Objects;


public class Epic extends Task {
    protected ArrayList<Integer> subtasksIds = new ArrayList<>();  //массив для хранения id подзадач____Таким образом мы связываем епики с подзадачами

    public Epic(String name, int id, Status status) {
        super(name, id, status);
    }

    public void addSubtask(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasksIds.equals(epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }

}
