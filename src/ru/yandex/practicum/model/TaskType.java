package ru.yandex.practicum.model;

public enum TaskType {
    TASK("Task"),
    EPIC("Epic"),
    SUBTASK("Subtask");

    private final String type;

    private TaskType(String s) {
        type = s;
    }

    public String toString() {
        return this.type;
    }

    public boolean equalsType(String otherType) {
        return type.equals(otherType);
    }
}
