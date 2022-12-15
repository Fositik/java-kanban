package ru.yandex.practicum.state;

public class TaskDone implements Status{
    @Override
    public String stage() {
        return "DONE";
    }
}
