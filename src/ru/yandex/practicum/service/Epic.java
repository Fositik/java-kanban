package ru.yandex.practicum.service;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    /*    Теперь массив хранит не id подзадач, а сами подзадачи
    Так как раньше, при вызове метода getAllSubtasksByEpicId мы получали только айди подзадач, но не сами задачи
     */
    protected ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name){
        super(name);
    }

    public Epic(String name,String description){
        super(name,description);
    }

    public Epic(String name, String description, Status status){
        super(name, description, status);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    /*  Было решено перенести методы для проверки и обновления статусов подзадач в класс Epic
        Так как у меня был метод sincEpic в классе InMemoryTaskManager и я не знал, какие параметры ему передать,
        после удаления переменной epicId, то я решил, что было бы удобнее перенести эти методы в данный класс.
         */
    public void checkEpicStatusInProgresss() {
        boolean checkStatus = true;
        for (Subtask subtask : subtasks) {
            if (!subtask.getStatus().equals(Status.IN_PROGRESS)) {
                checkStatus = false;
                break;
            }
        }
        if (checkStatus) {
            System.out.println("Эпик под id = " + id + " выполняется!");
            status = Status.IN_PROGRESS;
        }
    }

    public void checkEpicStatusDone() {
        //булевая переменная для проверки статуса эпика на DONE
        boolean checkStatus = true;
        //пробегаемся по всем подзадачам
        for (Subtask subtask : subtasks) {
            //если подзадча имеет статус отличный от DONE
            if (!subtask.getStatus().equals(Status.DONE)) {
                //то статус эпика не DONE
                checkStatus = false;
                break;
            }
        }
        //если же, все задачи имеют статус DONE
        if (checkStatus) {
            //то статус эпика тоже считается таким же
            status = Status.DONE;
            System.out.println("Эпик под id = " + id + "выполнен!");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
