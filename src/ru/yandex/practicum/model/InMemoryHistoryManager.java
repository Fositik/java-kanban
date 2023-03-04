package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    //Создавем связанный список для хранения истории просмотров.
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    //Метод для добавления очередной просмотренной задачи в Связанный список history
    public void add(Task task) {
        if (history.map.containsKey(task.getId())) {                //Если в списке уже содержится такая задача,
            remove(task.getId());                                   //удаляем предыдущую запись
        }
        history.linkLast(task);                                     //и добавляем ее в конец
      //  System.out.println(history.map.values());
    }

    @Override
    public void remove(int id) {
        if (!history.map.isEmpty()) {                //Если мапа не пустая
            history.removeNode(history.map.get(id)); //Удаляем ссылки из Связанного списка на ноду
            history.map.remove(id);                  //Также удаляем ноду из мапы
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    /**
     * Кастомный LinkedList
     * @param <E>
     */
    private static class CustomLinkedList<E extends Task> {
        private Node<E> head; //поле для головного элемента
        private Node<E> tail; //поле для хвоста
        private int size = 0; //длина списка
        private final Map<Integer, Node<E>> map = new HashMap<>(); //мапа, которая в ключе хранит id задачи, а в значении узел связанного списка

        private void linkLast(E value) {
            if (head==null) {                          //В первую очередь, проверям, пуст ли наш связанный список
                Node<E> currentNode = new Node<>(value); //Новая нода
                map.put(value.getId(), currentNode);     //Добавляем новую ноду в мапу
                head = currentNode;                      //Текущая нода - это голова списка
            } else if (size == 1) {                      //Если же в списке всего один элемент
                Node<E> currentNode = new Node<>(value); //Новая нода
                head.setNext(currentNode);               //Связываем голову с нашей новой нодой
                map.put(value.getId(), currentNode);     //Добавляем в мапу новую ноду
                currentNode.setPrev(head);               //Наша новая нода ссылается на предыдущую - голову списка
                tail = currentNode;                      //Хвост спика - новая нода
            } else {
                Node<E> currentNode = new Node<>(value); //Новая нода
                currentNode.setPrev(tail);               //Новая нода ссылается на предыдущий элемент - хвос списка
                map.put(value.getId(), currentNode);     //Добавляем в мапу новую ноду
                tail.setNext(currentNode);               //Связываем предыдущих вост с новой нодой
                tail = currentNode;                      //Теперь новая нода - хвост списка
            }
            ++size;                                      //увеличиваем счетчик длины списка на 1 --> используем префиксный инкремент
        }

        private List<E> getTasks() {
            if (head != null) {                             //Если список не пустой
                List<E> tasks = new ArrayList<>();
                Node<E> currentNode = head;                 //поле для хранения текущей ноды. Нужно, чтобы пробежаться по списку
                while (true) {
                    tasks.add(currentNode.getValue());      //добавляем значение текущей ноды
                    if (currentNode.getNext() == null)      //если следующего элемента не существует
                        break;
                    currentNode = currentNode.getNext();    //переходим к следующей ноде
                }
                return tasks;
            } else
                return null;                                //Если пустой, то возвращаем Null
        }

        private void removeNode(Node<E> value) {
            if(size==0)
                return;
            else if (size ==1) {                     //Если список пуст
                head = null;
            } else if (head == value) {             //Если нода, которую хотим удалить - голова
                if (size == 2) {                    //Если список содержит всего две ноды
                    tail.setPrev(null);             //Отвязываем голову и хвост
                    head.setNext(null);
                    head = tail;                    //голова равна хвосту
                    tail = null;                    //а хвост равен Null
                } else {
                    head = head.getNext();          //голова теперь равна следующему элементу
                    head.getPrev().setNext(null);   //Убираем ссылки на ноду, которую хотим удалить
                    head.setPrev(null);
                }
            } else if (tail == value) {             //Если искомая нода - хвост
                if (size == 2) {                    //Делаем пости все то же самое, что и в пердыдущем случае
                    tail.setPrev(null);
                    tail = null;
                    head.setNext(null);
                } else {
                    tail = tail.getPrev();
                    tail.getNext().setPrev(null);
                    tail.setNext(null);
                }
            } else {                                        //Во всех остальных случаях просто
                value.getPrev().setNext(value.getNext());   //переписываем ссылки на предыдущую и следующую ноду
                value.getNext().setPrev(value.getPrev());
                value.setNext(null);                        //и отвязываем нашу текующую ноду
                value.setPrev(null);
            }
            --size;                                             //Уменьшаем длину списка на 1 --> используем префиксный декремент
        }


        /**
         * Класс Node<E> для элеменотв узла.
         * * @param <E>
         */
        private static class Node<E> {
            private final E value;                      //для данных внутри элемента
            private Node<E> prev;                       //поле для ссылки на предыдущий элемент
            private Node<E> next;                       //поле для ссылки на следующий элемент

            private Node(E task) {
                this.value = task;
            }

            public E getValue() {
                return value;
            }

            public Node<E> getPrev() {
                return prev;
            }

            public void setPrev(Node<E> prev) {
                this.prev = prev;
            }

            public Node<E> getNext() {
                return next;
            }

            public void setNext(Node<E> next) {
                this.next = next;
            }
        }
    }
}



