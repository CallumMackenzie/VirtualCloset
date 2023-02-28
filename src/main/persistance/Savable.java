package persistance;

import org.json.JSONObject;

// A savable data type
public interface Savable {

    // EFFECTS: Returns a JSON representation of this object.
    JSONObject toJson();
}
