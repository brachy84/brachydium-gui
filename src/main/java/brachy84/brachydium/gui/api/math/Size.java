package brachy84.brachydium.gui.api.math;

import brachy84.brachydium.gui.api.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

public class Size implements JsonReader {

    public static final Size ZERO = new Size(0, 0);

    public float width, height;

    public Size(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void resize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public boolean isLargerThan(Size size) {
        return ((width - size.width) + (height - size.height)) > 0;
    }

    /**
     * @param size to center
     * @return the point of the top left corner
     */
    public Pos2d getCenteringPointForChild(Size size) {
        return new Pos2d((width - size.width) / 2, (height - size.height) / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return Float.compare(size.width, width) == 0 && Float.compare(size.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public Size readJson(JsonElement json) {
        float width = 0, height = 0;
        if(json.isJsonObject()) {
            JsonObject j = json.getAsJsonObject();
            if(j.has("width")) width = j.get("width").getAsFloat();
            if(j.has("height")) height = j.get("height").getAsFloat();
        } else if(json.isJsonArray()) {
            JsonArray j = json.getAsJsonArray();
            if(j.size() <= 1) {
                width = j.get(0).getAsFloat();
            }
            if(j.size() <= 2) {
                height = j.get(1).getAsFloat();
            }
        }
        this.width = width;
        this.height = height;
        return new Size(width, height);
    }
}
