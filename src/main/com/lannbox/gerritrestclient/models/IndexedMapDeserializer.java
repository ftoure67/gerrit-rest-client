package com.lannbox.gerritrestclient.models;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class IndexedMapDeserializer<T>  implements JsonDeserializer<Map<String, T>> {
    private Field indexField;

    public IndexedMapDeserializer(String indexFieldName, Class<T> clazz) throws NoSuchFieldException {
        indexField = clazz.getDeclaredField(indexFieldName);
    }

    @Override
    public Map<String, T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, T> map = new HashMap<String, T>(jsonObject.entrySet().size());
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String index = entry.getKey();
            T value = context.deserialize(entry.getValue(), indexField.getDeclaringClass());
            try {
                indexField.set(value, index);
            } catch (IllegalAccessException e) {
                throw new JsonParseException(e);
            }
            map.put(index, value);
        }
        return map;
    }
}
