package ru.yandex.practicum;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        //Задача id 1
        SimpleTask takeExams = new SimpleTask("Сдать Экзамены", taskManager.nextId,
                "сдать экзамены на 5", Status.NEW);
        taskManager.addSimpleTask(takeExams);

        //Задача id 2
        SimpleTask pickUpTheChild = new SimpleTask("Забрать ребенка", taskManager.nextId,
                "Забрать Петю из детского сада", Status.NEW);
        taskManager.addSimpleTask(pickUpTheChild);

        //Получаем список всех задач
        System.out.println("Список всех задач: " + taskManager.getAllSimpleTasks());
        //Удаляем задачу id 1
        taskManager.removeSimbletaskById(1);
        //Получаем список всех задач, смотрим изменения
        System.out.println("Список всех задач: " + taskManager.getAllSimpleTasks());
        taskManager.updateSimpleTaskById(2, new SimpleTask("Task", 1, "Update Task", Status.NEW));
        //Эпик id 3
        Epic buyProducts = new Epic("Купить продукты", taskManager.nextId, Status.NEW);
        taskManager.addEpic(buyProducts);
        System.out.println("Задача под id= 2: " + taskManager.getSimpleTaskById(2));
        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask("Milk", taskManager.nextId, "Fat content 3.2", 3, Status.NEW);
        taskManager.addSubtask(milk, buyProducts);
        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask("Bread", taskManager.nextId, "wheat", 3, Status.IN_PROGRESS);
        taskManager.addSubtask(bread, buyProducts);

        taskManager.updateSubtask(4,new Subtask("UpdateSub",1,"SubWasUpdated",
                1,Status.IN_PROGRESS));
        //Получаем список всех подзадач эпика id 3
        System.out.println("Список поздадач для эпика id = 3: " + taskManager.getAllSubtasksByEpic(3));
        //Получаем подзадачу id 4 эпикка id 3
        System.out.println("Подзадача под id = 4:" + taskManager.getSubtaskById(4));

        //Эпик id 6
        Epic doTheLessons = new Epic("Сделать уроки", taskManager.nextId, Status.NEW);
        taskManager.addEpic(doTheLessons);
        //Подзадача id 7, эпик id 6
        Subtask biology = new Subtask("Biology", taskManager.nextId,
                "cell division", 6, Status.NEW);
        taskManager.addSubtask(biology, doTheLessons);
        //Подзадача id 8, эпик id 6
        Subtask math = new Subtask("Math", taskManager.nextId, "to solve the task", 6, Status.NEW);
        taskManager.addSubtask(math, doTheLessons);

        ////Получаем список всех эпиков
        System.out.println("Список всех эпиков: " + taskManager.getAllEpics());
        //Удаляем эпик id 3 и все его подзадачи
        taskManager.removeEpicById(3);
        //Получаем список всех эпиков, смотрим изменения
        System.out.println("Список всех эпиков: " + taskManager.getAllEpics());
        //Получаем id подзадач эпика id 6
        System.out.println("Список поздадач для эпика id = 6: " + taskManager.getAllSubtasksByEpic(6));
        //Удаляем все задачи/эпики/подзадачи
        taskManager.removeAllTasks();
        System.out.println(taskManager.getAllSimpleTasks());
        System.out.println(taskManager.getAllEpics());
    }
}