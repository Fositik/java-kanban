package ru.yandex.practicum.http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс `TypeAdapter` в Java является частью библиотеки Gson и предоставляет пользователю универсальный
 * способ сериализации и десериализации объектов Java в JSON и обратно.
 * <p>
 * Он работает путем определения способа преобразования объекта Java в его эквивалентное представление
 * в JSON и наоборот. Класс `TypeAdapter` является абстрактным, поэтому вам нужно создать свой собственный
 * класс на основе него и реализовать необходимые методы.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String time = in.nextString();
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm"));
    }

    @Override
    public void write(JsonWriter writer, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            writer.nullValue();
            return;
        }
        String time = localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm"));
        writer.value(time);
    }

}
