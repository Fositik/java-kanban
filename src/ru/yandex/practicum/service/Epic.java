package ru.yandex.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Продолжительность эпика — сумма продолжительности всех его подзадач.
 * з.ы. -- метод для подсчета продолжительности эпика
 * Время начала — дата старта самой ранней подзадачи, а время завершения — время окончания самой поздней из задач.
 * з.ы. --
 * Новые поля duration и startTime этого класса будут расчётные — аналогично полю статус.
 * Для реализации getEndTime() удобно добавить поле endTime в Epic и рассчитать его вместе с другими полями.
 */
public class Epic extends Task {

    /*    Теперь массив хранит не id подзадач, а сами подзадачи
    Так как раньше, при вызове метода getAllSubtasksByEpicId мы получали только айди подзадач, но не сами задачи
     */
    protected ArrayList<Subtask> subtasks = new ArrayList<>();
    //Для реализации getEndTime() удобно добавить поле endTime в Epic и рассчитать его вместе с другими полями.
    private LocalDateTime endTime;

    public Epic(String name) {
        super(name);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status) {
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
        int checkStatus = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                status = Status.IN_PROGRESS;
            } else if (subtask.getStatus().equals(Status.DONE)) {
                checkStatus++;
                if (subtasks.size() > 1 && checkStatus == 1) {
                    status = Status.IN_PROGRESS;
                }
            }
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
            System.out.println("Эпик под id = " + id + " выполнен!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasks.equals(epic.subtasks) && endTime.equals(epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks, endTime);
    }
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        Epic epic = (Epic) o;
//        return subtasks.equals(epic.subtasks);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), subtasks);
//    }

    public void calculateStatrTimeAndDuration() {
        duration = null;                                                //Поле нужно на случай того, если були добавлены новые подзадачи в эпик
        startTime = null;                                               //Либо же, были удалены старые
        if (!subtasks.isEmpty()) {                                      //Если список подзадач не пустой
            for (Subtask subtask : subtasks) {                          //Проходимся по подзадачам
                if (subtask.getDuration() != null) {
                    if (duration == null) {                             //Пришлось добавить, так как по умолчанию они считались null
                        duration = subtask.getDuration();
                    } else
                        duration = duration.plus(subtask.getDuration());    //Находим общую продолжительность подзадачи путем суммирования
                }
            }
            startTime = startDateTimeOfTheEarliestSubtask();
        }
    }

    private LocalDateTime startDateTimeOfTheEarliestSubtask() {
        LocalDateTime earliestSubtaskStartTime = subtasks.get(0).getStartTime(); //Дата старта самой первой подзадачи
        if (subtasks.size() > 1) {                                               //Если больше одной подзадачи
            for (int i = 0; i < subtasks.size(); i++) {                          //Проходимся по всем
                if (earliestSubtaskStartTime == null)                            //Еслидата страрта до сих пор не определена
                    earliestSubtaskStartTime = subtasks.get(i).getStartTime();
                else if (subtasks.get(i).getStartTime() != null                  //Если же есть задача, у которой время старта определена раньше
                        && subtasks.get(i).getStartTime().isBefore(earliestSubtaskStartTime))
                    earliestSubtaskStartTime = subtasks.get(i).getStartTime();
            }
        }
        return earliestSubtaskStartTime;
    }

    private LocalDateTime endDataTimeOfTheLatestSubtask() {
        LocalDateTime lateSubtaskEndTime = subtasks.get(0).getEndTime();
        if (subtasks.size() > 1) {
            for (int i = 0; i < subtasks.size(); i++) {
                if (lateSubtaskEndTime == null)
                    lateSubtaskEndTime = subtasks.get(i).getEndTime();
                else if (subtasks.get(i).getEndTime() != null
                        && subtasks.get(i).getEndTime().isAfter(lateSubtaskEndTime))
                    lateSubtaskEndTime = subtasks.get(i).getEndTime();
            }
        }
        return lateSubtaskEndTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endDataTimeOfTheLatestSubtask();
    }
}
