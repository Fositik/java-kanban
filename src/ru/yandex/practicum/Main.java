package ru.yandex.practicum;

import ru.yandex.practicum.Task;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        SimpleTask takeExams = new SimpleTask("Сдать Экзамены", taskManager.nextId, "сдать экзамены на 5", Status.NEW);
        taskManager.addSimpleTask(takeExams);

        Epic buyProducts = new Epic("Купить продукты", taskManager.nextId, Status.NEW);
        taskManager.addEpic(buyProducts);

        Subtask milk = new Subtask("Milk", taskManager.nextId, "Fat content 3.2", 2, Status.NEW);
        taskManager.addSubtask(milk);
        Subtask bread = new Subtask("Bread", taskManager.nextId, "wheat", 2, Status.NEW);
        taskManager.addSubtask(bread);


        Epic doTheLessons = new Epic("Сделать уроки", taskManager.nextId, Status.NEW);
        taskManager.addEpic(doTheLessons);

        Subtask biology = new Subtask("Biology", taskManager.nextId, "cell division", 5, Status.NEW);
        taskManager.addSubtask(biology);
        Subtask math = new Subtask("Math", taskManager.nextId, "to solve the task", 5, Status.NEW);
        taskManager.addSubtask(math);

        taskManager.deletingAllTasks();


    }
}