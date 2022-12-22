package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        //Задача id 1
        Task takeExams = new Task(taskManager.nextId,"Сдать Экзамены",
                "сдать экзамены на 5", Status.NEW);
        taskManager.addSimpleTask(takeExams);

        //Задача id 2
        Task pickUpTheChild = new Task(taskManager.nextId,"Зaбрать ребенка",
                "Забрать Петю из детского сада", Status.NEW);
        taskManager.addSimpleTask(pickUpTheChild);

        //Получаем список всех задач
        System.out.println("Список всех задач: " + taskManager.getAllSimpleTasks());
        //Удаляем задачу id 1
        taskManager.removeSimbletaskById(1);
        //Получаем список всех задач, смотрим изменения
        System.out.println("Список всех задач: " + taskManager.getAllSimpleTasks());
        taskManager.updateSimpleTaskById(2, new Task(1,"Task" , "Update Task", Status.NEW));
        //Эпик id 3
        Epic buyProducts = new Epic(taskManager.nextId,"Купить продукты", "Description", Status.NEW);
        taskManager.addEpic(buyProducts);
        System.out.println("Задача под id= 2: " + taskManager.getSimpleTaskById(2));
        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask(taskManager.nextId,"Milk",  "Fat content 3.2", Status.NEW, 3, buyProducts);
        taskManager.addSubtask(milk);
        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask(taskManager.nextId,"Bread",  "wheat",  Status.IN_PROGRESS,3, buyProducts);
        taskManager.addSubtask(bread);
        System.out.println("Список всех эпиков: " + taskManager.getAllEpics());
        taskManager.updateSubtask(4,new Subtask(1,"UpdateSub","SubWasUpdated",
                Status.IN_PROGRESS,1,buyProducts));
        //Получаем список всех подзадач эпика id 3
        System.out.println("Список поздадач для эпика id = 3: " + taskManager.getAllSubtasksByEpic(3));
        //Получаем подзадачу id 4 эпикка id 3
        System.out.println("Подзадача под id = 4:" + taskManager.getSubtaskById(4));

        //Эпик id 6
        Epic doTheLessons = new Epic(taskManager.nextId,"Сделать уроки","Description", Status.NEW);
        taskManager.addEpic(doTheLessons);
        //Подзадача id 7, эпик id 6
        Subtask biology = new Subtask(taskManager.nextId,"Biology",
                "cell division",  Status.NEW,6,doTheLessons);
        taskManager.addSubtask(biology);
        //Подзадача id 8, эпик id 6
        Subtask math = new Subtask(taskManager.nextId,"Math",  "to solve the task",  Status.NEW, 6, doTheLessons);
        taskManager.addSubtask(math);

        ////Получаем список всех эпиков
        System.out.println("Список всех эпиков: " + taskManager.getAllEpics());
        //Удаляем эпик id 3 и все его подзадачи
        taskManager.removeEpicById(3);
        //Получаем список всех эпиков, смотрим изменения
        System.out.println("Список всех эпиков: " + taskManager.getAllEpics());
        //Получаем id подзадач эпика id 6
        System.out.println("Список поздадач для эпика id = 6: " + taskManager.getAllSubtasksByEpic(6));

        //Удаляем все задачи/эпики/подзадачи
        taskManager.removeAllSimpleTasks();
        taskManager.removeAllSubtasks();
        taskManager.getAllEpics();
        System.out.println(taskManager.getAllSimpleTasks());
        System.out.println(taskManager.getAllEpics());
    }
}