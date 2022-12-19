package ru.yandex.practicum;

import ru.yandex.practicum.Task;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        SimpleTask takeExams = new SimpleTask("Сдать Экзамены", taskManager.nextId,
                "сдать экзамены на 5", Status.NEW);
        taskManager.addSimpleTask(takeExams);

        SimpleTask pickUpTheChild = new SimpleTask("Забрать ребенка", taskManager.nextId,
                "Забрать Петю из детского сада", Status.NEW);
        taskManager.addSimpleTask(pickUpTheChild);

        taskManager.getAllSimpleTasks();

        Epic buyProducts = new Epic("Купить продукты", taskManager.nextId, Status.NEW);
        taskManager.addEpic(buyProducts);

        Subtask milk = new Subtask("Milk", taskManager.nextId, "Fat content 3.2", 3, Status.NEW);
        taskManager.addSubtask(milk, buyProducts);
        Subtask bread = new Subtask("Bread", taskManager.nextId, "wheat", 3, Status.IN_PROGRESS);
        taskManager.addSubtask(bread, buyProducts);

        Epic doTheLessons = new Epic("Сделать уроки", taskManager.nextId, Status.NEW);
        taskManager.addEpic(doTheLessons);

        Subtask biology = new Subtask("Biology", taskManager.nextId,
                "cell division", 6, Status.NEW);
        taskManager.addSubtask(biology, doTheLessons);
        Subtask math = new Subtask("Math", taskManager.nextId, "to solve the task", 6, Status.NEW);
        taskManager.addSubtask(math, doTheLessons);

        taskManager.getAllTasks();

        taskManager.deletingAllTasks();


    }
}