package ru.yandex.practicum;

public class Subtask extends Task {
    protected String description;   //у подзадач есть описание
    protected int epicId;           //переменная для хранения идентификатора эпика

    public Subtask(String name, int id, String description, int epicId, Status status) {   //конструктор для подзадач
        super(name, id, status);
        this.description = description;
        this.epicId = epicId;
    }

    public int getEpicId(){
        return epicId;
    }

}
