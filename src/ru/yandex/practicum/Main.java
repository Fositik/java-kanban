package ru.yandex.practicum;

import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

public class Main {
    public static void main(String[] args) {

         int id=1;
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        //Задача id 1
        Task takeExams = new Task(id, "Сдать Экзамены",
                "сдать экзамены на 5", Status.NEW);
        inMemoryTaskManager.addSimpleTask(takeExams);


        //Задача id 2
        Task pickUpTheChild = new Task(id, "Зaбрать ребенка",
                "Забрать Петю из детского сада", Status.NEW);
        inMemoryTaskManager.addSimpleTask(pickUpTheChild);

        //Получаем список всех задач
        System.out.println("Список всех задач: " + inMemoryTaskManager.getAllSimpleTasks());

        inMemoryTaskManager.updateSimpleTaskById(2, new Task(id, "Task", "Update Task", Status.NEW));
        //Получаем список всех задач, смотрим изменения
        System.out.println("Список всех задач: " + inMemoryTaskManager.getAllSimpleTasks());


        //Эпик id 3
        Epic buyProducts = new Epic(id, "Купить продукты", "Description", Status.NEW);
        inMemoryTaskManager.addEpic(buyProducts);
        System.out.println("Задача под id= 2: " + inMemoryTaskManager.getSimpleTaskById(2));                                            //1
        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask(id, "Milk", "Fat content 3.2", Status.NEW,
                 buyProducts);
        inMemoryTaskManager.addSubtask(milk, buyProducts);
        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask(id, "Bread", "wheat", Status.IN_PROGRESS,
                 buyProducts);
        inMemoryTaskManager.addSubtask(bread, buyProducts);
        System.out.println("Список всех эпиков: " + inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.updateSubtask(4, new Subtask(id, "UpdateSub", "SubWasUpdated",
                Status.IN_PROGRESS,  buyProducts));
        //Получаем список всех подзадач эпика id 3
        System.out.println("Список поздадач для эпика id = 3: " + inMemoryTaskManager.getAllSubtasksByEpic(3));
        //Получаем подзадачу id 4 эпика id 3
        System.out.println("Подзадача под id = 4:" + inMemoryTaskManager.getSubtaskById(4));                                    //2

        //Эпик id 6
        Epic doTheLessons = new Epic(id, "Сделать уроки", "Description", Status.NEW);
        inMemoryTaskManager.addEpic(doTheLessons);
        //Подзадача id 7, эпик id 6
        Subtask biology = new Subtask(id, "Biology",
                "cell division", Status.NEW,  doTheLessons);
        inMemoryTaskManager.addSubtask(biology, doTheLessons);
        //Подзадача id 8, эпик id 6
        Subtask math = new Subtask(id, "Math", "to solve the task", Status.NEW,  doTheLessons);
        inMemoryTaskManager.addSubtask(math, doTheLessons);

        ////Получаем список всех эпиков
        System.out.println("Список всех эпиков: " + inMemoryTaskManager.getAllEpics());

        //Получаем список всех эпиков, смотрим изменения
        System.out.println("Список всех эпиков: " + inMemoryTaskManager.getAllEpics());
        //Получаем id подзадач эпика id 6
        System.out.println("Список поздадач для эпика id = 6: " + inMemoryTaskManager.getAllSubtasksByEpic(6));

        inMemoryTaskManager.getEpicById(6);                                                                                                //3
        inMemoryTaskManager.getSubtaskById(8);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getSimpleTaskById(1);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(6);
        inMemoryTaskManager.getSubtaskById(7);
        System.out.println( historyManager.getHistory());


    }
}