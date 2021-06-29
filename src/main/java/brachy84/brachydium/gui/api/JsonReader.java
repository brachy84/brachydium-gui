package brachy84.brachydium.gui.api;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface JsonReader<T> {

    T readJson(JsonElement json);
}
