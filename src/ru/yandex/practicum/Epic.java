package ru.yandex.practicum;



import java.util.ArrayList;


public class Epic extends Task {
    protected ArrayList<Integer> subtasksIds= new ArrayList<>();  //массив для хранения id подзадач____Таким образом мы связываем епики с подзадачами

    public Epic(String name, int id, Status status) {    //эпику описание не нужно
        super(name, id, status);
    }

    public void addTask(int subtaskId) {
        subtasksIds.add(subtaskId);
    }
   public ArrayList<Integer> getSubtasksIds(){
        return subtasksIds;
   }
}
