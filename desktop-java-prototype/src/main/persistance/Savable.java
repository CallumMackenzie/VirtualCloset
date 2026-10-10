package persistance;

import org.json.JSONObject;

// A savable data type
@FunctionalInterface
public interface Savable<T> {

    // EFFECTS: Returns a JSON representation of this object.
    JSONObject toJson(T args);
}
