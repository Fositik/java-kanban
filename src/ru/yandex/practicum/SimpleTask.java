package ru.yandex.practicum;


public class SimpleTask extends Task {
  protected String description;    //у простой задачи должно быть описание

    public SimpleTask(String name, int id, String description, Status status) {
        super(name, id, status);
        this.description = description;
    }
}
