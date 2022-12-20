# java-kanban
Repository for homework project.
Менеджер
Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера. Он будет запускаться на старте программы и управлять всеми задачами. В нём должны быть реализованы следующие функции:
++Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
Методы для каждого из типа задач(Задача/Эпик/Подзадача):
++Получение списка всех задач.
++Удаление всех задач.
++Получение по идентификатору.
++Создание. Сам объект должен передаваться в качестве параметра.
++Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
++Удаление по идентификатору.
Дополнительные методы:
++Получение списка всех подзадач определённого эпика.
Управление статусами осуществляется по следующему правилу:
++Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
Для эпиков:
++если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
++если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
++во всех остальных случаях статус должен быть IN_PROGRESS.