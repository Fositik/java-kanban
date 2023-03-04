package ru.yandex.practicum.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {             //общими чертами для всех задач являются:
    protected String name;      //имя самой задачи
    protected int id;           //уникальный идентификатор, по которому можно найти и управлять задачей
    protected String description;
    protected Status status;    //текущий статус задачи
    //Добавьте новые поля в задачи:
    protected Duration duration; //продолжительность задачи, оценка того, сколько времени она займёт в минутах (число);
    protected LocalDateTime startTime; //дата, когда предполагается приступить к выполнению задачи.
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");

    //Решил протестировать перегрузку и понял, что указывать id при создании новой задачи нет никакой необходимости.
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = null;
        this.duration = null;
    }

    //Перегрузка конструктора. Когда передаем имя и описание. Статус по умолчанию равен NEW
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = null;
        this.duration = null;
    }

    //Когда передаем только имя. Описание по умолчанию равно null, а сатаус - NEW
    public Task(String name) {
        this.name = name;
        this.description = null;
        this.status = Status.NEW;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = null;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    /**
     * getEndTime() — время завершения задачи, которое рассчитывается исходя из startTime и duration.
     */
    public LocalDateTime getEndTime() {
        if (startTime != null & duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDuration(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(String startTime) {
        if(startTime!=null)
        this.startTime = LocalDateTime.parse(startTime, formatter);
        else this.startTime = null;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    public void resetStartTimeAndDuration() {
        startTime = null;
        duration = null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }


}
