package ru.yandex.practicum.state;

public class NewTask implements Status {
    @Override
    public String stage() {
        return "NEW";
    }
}
