package ru.yandex.practicum.state;

public class TaskInProgress implements Status{
    @Override
    public String stage() {
        return  "IN_PROGRESS";

    }
}
