package ru.yandex.practicum;

import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.Epic;
import ru.yandex.practicum.service.Subtask;
import ru.yandex.practicum.service.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Задача id 1
        Task takeExams = new Task("Сдать Экзамены", LocalDateTime.now(), Duration.ofMinutes(30));
        inMemoryTaskManager.addSimpleTask(takeExams);

        //Задача id 2
        Task pickUpTheChild = new Task("Заехать в школу",LocalDateTime.now().plus(Duration.ofMinutes(21)), Duration.ofMinutes(30));
        inMemoryTaskManager.addSimpleTask(pickUpTheChild);
        System.out.println("Задача под id "+ pickUpTheChild.getId()+" : "+ inMemoryTaskManager.getSimpleTaskById(2));
        //Эпик id 3
        Epic buyProducts = new Epic("Купить продукты");
        inMemoryTaskManager.addEpic(buyProducts);

        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask("Молоко",LocalDateTime.now(), Duration.ofMinutes(43), buyProducts);
        inMemoryTaskManager.addSubtask(milk, buyProducts);

        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask("Хлеб",LocalDateTime.now().plus(Duration.ofMinutes(111)), Duration.ofMinutes(30), buyProducts);
        inMemoryTaskManager.addSubtask(bread, buyProducts);

        //Подзадача id 6, эпик id 3
        Subtask butter = new Subtask("Масло",LocalDateTime.now().plus(Duration.ofMinutes(153)), Duration.ofMinutes(30),buyProducts);
        inMemoryTaskManager.addSubtask(butter,buyProducts);

        //Эпик id 7
        Epic doTheLessons = new Epic("Сделать уроки");
        inMemoryTaskManager.addEpic(doTheLessons);

        //Подзадача id 8, эпик id 6
        Subtask biology = new Subtask("Биология",LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
        inMemoryTaskManager.addSubtask(biology, doTheLessons);

        //Подзадача id 9, эпик id 6
        Subtask math = new Subtask("Математика",LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
        inMemoryTaskManager.addSubtask(math, doTheLessons);
        Subtask adf = new Subtask("Математика2",LocalDateTime.now().plus(Duration.ofMinutes(256)), Duration.ofMinutes(30), doTheLessons);
        inMemoryTaskManager.addSubtask(adf, doTheLessons);

        Task takeExams2 = new Task("Сдать Экзамены");
        inMemoryTaskManager.addSimpleTask(takeExams2);

        System.out.println(inMemoryTaskManager.getAllSubtasksByEpic(7));
        inMemoryTaskManager.removeAllSubtasks();
        System.out.println(inMemoryTaskManager.getAllSubtasksByEpic(7));
        /**
         * Итого, порядок должен быть следующим: 2->1->3->7->5->4->6->8->9
         */
        System.out.println("История: " + historyManager.getHistory());


        /**
         * Удаляем эпик 3, а вместе с ним и подзадачи 4,5,6
         * Итого, порядок должен быть следующим: 2->1->7->8->9
         */
        System.out.println("История: " + historyManager.getHistory());
        System.out.println("История: "+ inMemoryTaskManager.getPrioritizedTasks());
    }
}