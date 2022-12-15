package ru.yandex.practicum.state;

public class TaskStatus {
    Status status;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus() {
        if (status instanceof NewTask) {
            setStatus(new TaskInProgress());
        } else if (status instanceof TaskInProgress){
            setStatus(new TaskDone());
        } else {
            setStatus(new TaskDone());
        }
    }
    public void Status(){
        status.stage();
    }
}
