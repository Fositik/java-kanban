package ru.yandex.practicum;

import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Задача id 1
        Task takeExams = new Task("Сдать Экзамены");
        inMemoryTaskManager.addSimpleTask(takeExams);
        System.out.println("Обновляем задачу под id " + takeExams.getId() + " : "
                + inMemoryTaskManager.updateSimpleTaskById(1, new Task("Сдать экзамены на отлично!",
                "Выучить все предметы", Status.IN_PROGRESS)));
        System.out.println("Задача под id " + takeExams.getId() + " : " + takeExams);

        //Задача id 2
        Task pickUpTheChild = new Task("Заехать в школу");
        inMemoryTaskManager.addSimpleTask(pickUpTheChild);
        inMemoryTaskManager.updateSimpleTaskById(2, new Task("Заехать в школу", "Забрать Вову",
                Status.DONE));
        //Получаем список всех задач, смотрим изменения
        System.out.println("Список всех задач: " + inMemoryTaskManager.getAllSimpleTasks());

        //Эпик id 3
        Epic buyProducts = new Epic("Купить продукты");
        inMemoryTaskManager.addEpic(buyProducts);
        System.out.println("Задача под id= " + buyProducts.getId() + " : "
                + inMemoryTaskManager.getSimpleTaskById(2));

        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask("Молоко", buyProducts);
        inMemoryTaskManager.addSubtask(milk, buyProducts);

        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask("Хлеб", buyProducts);
        inMemoryTaskManager.addSubtask(bread, buyProducts);

        //Обновляем подзадачу под id 4
        inMemoryTaskManager.updateSubtask(4, new Subtask("Хлеб", "Пшеничный",
                Status.DONE, buyProducts));
        //Обновляем подзадачу под id 5
        inMemoryTaskManager.updateSubtask(5, new Subtask("Молоко", "Пастеризованное",
                Status.DONE, buyProducts));
        //Получаем список всех подзадач эпика id 3
        System.out.println("Список поздадач для эпика id = 3: " + buyProducts.getSubtasks());
        //Получаем подзадачу id 4 эпика id 3
        System.out.println("Подзадача под id = 4:" + inMemoryTaskManager.getSubtaskById(4));

        //Эпик id 6
        Epic doTheLessons = new Epic("Сделать уроки");
        inMemoryTaskManager.addEpic(doTheLessons);

        //Подзадача id 7, эпик id 6
        Subtask biology = new Subtask("Биология", doTheLessons);
        inMemoryTaskManager.addSubtask(biology, doTheLessons);
        inMemoryTaskManager.updateSubtask(7, new Subtask("Биология", "Подготовить доклад",
                Status.IN_PROGRESS, doTheLessons));
        //Подзадача id 8, эпик id 6
        Subtask math = new Subtask("Математика", doTheLessons);
        inMemoryTaskManager.addSubtask(math, doTheLessons);

        ////Получаем список всех эпиков
        System.out.println("Список всех эпиков: " + inMemoryTaskManager.getAllEpics());


        inMemoryTaskManager.getEpicById(6);                                                                                                //3
        inMemoryTaskManager.getSubtaskById(8);
        inMemoryTaskManager.getSubtaskById(4);
        inMemoryTaskManager.getSimpleTaskById(1);
        inMemoryTaskManager.getEpicById(3);
        inMemoryTaskManager.getEpicById(6);
        inMemoryTaskManager.getSubtaskById(7);
        System.out.println("История: " + historyManager.getHistory());


    }
}