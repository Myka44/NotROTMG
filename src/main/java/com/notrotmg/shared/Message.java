package com.notrotmg.shared;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public interface Message {
    String getType();

    default String toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getType());
        String json = new Gson().toJson(this);
        JsonObject fields = JsonParser.parseString(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : fields.entrySet()) {
            obj.add(entry.getKey(), entry.getValue());
        }
        return obj.toString();
    }

    static Message fromJson(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj == null) return null;
            JsonElement typeElement = obj.get("type");
            if (typeElement == null || typeElement.isJsonNull()) return null;
            String type = typeElement.getAsString();
            return switch (type) {
                case "player_join" -> new Gson().fromJson(json, PlayerJoin.class);
                case "player_leave" -> new Gson().fromJson(json, PlayerLeave.class);
                case "state_snapshot" -> new Gson().fromJson(json, StateSnapshot.class);
                case "input" -> new Gson().fromJson(json, PlayerInput.class);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
}
