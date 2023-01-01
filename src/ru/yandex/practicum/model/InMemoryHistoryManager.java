package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //Создавем связанный список для хранения истории просмотров.
    private static LinkedList<Task> hsitory = new LinkedList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    //Метод для добавления очередной просмотренной задачи в Связанный список history
    public void add(Task task) {
        hsitory.addLast(task);
        //Если размер спика больше или равно 10, удаляем первый элемент
        if (hsitory.size() >= HISTORY_SIZE) {
            //Удаляем первый элемент списка (unlinkFirst(node))
            hsitory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return hsitory; //просто возвращаем
    }

}
