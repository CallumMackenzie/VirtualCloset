package persistance;

import org.json.JSONArray;
import org.json.JSONObject;

// A JSONObject wrapper for Savable objects
public class JsonBuilder extends JSONObject {

    // MODIFIES: this
    // EFFECTS: Adds the given savable at the given key
    public JsonBuilder savable(String key, Savable s) {
        this.put(key, s.toJson());
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Adds the given savable list to this JSONObject at the given key
    public <T extends Savable> JsonBuilder savable(String key, Iterable<T> s) {
        this.put(key, savableArray(s));
        return this;
    }

    // EFFECTS: Returns a JSONArray of the given Savable
    public static <T extends Savable> JSONArray savableArray(Iterable<T> s) {
        JSONArray jsa = new JSONArray();
        for (Savable savable : s) {
            jsa.put(savable.toJson());
        }
        return jsa;
    }

}
