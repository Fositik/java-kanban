package ru.yandex.practicum.http.gson.deserialize;

import com.google.gson.*;
import ru.yandex.practicum.service.*;


import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskJsonDeserializer implements JsonDeserializer<Subtask> {
    @Override
    public Subtask deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Subtask subtask = new Subtask(jsonObject.get("Name").getAsString());
        if (!jsonObject.get("Specification").getAsString().equals("null"))
            subtask.setDescription(jsonObject.get("Specification").getAsString());

        subtask.setId(jsonObject.get("Id").getAsInt());
        subtask.setStatus(Status.valueOf(jsonObject.get("Status").getAsString()));

        if (!jsonObject.get("Duration").getAsString().equals("null"))
            subtask.setDuration(context.deserialize(jsonObject.get("Duration"), Duration.class));

        if (!jsonObject.get("StartTime").getAsString().equals("null"))
            subtask.setStartTime((String) context.deserialize(jsonObject.get("StartTime"), LocalDateTime.class));

        subtask.setEpicId(jsonObject.get("IdEpic").getAsInt());

        return subtask;
    }
}