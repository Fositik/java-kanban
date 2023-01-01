package ru.yandex.practicum.service;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    //массив для хранения id подзадач____Таким образом мы связываем епики с подзадачами
    protected ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
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
