package persistance;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// Serializes application data to a JSON format at the given path
public class JsonWriter {

    public final String dstPath;

    // EFFECTS: Constructs a new JsonWriter to the given path
    public JsonWriter(String path) {
        this.dstPath = path;
    }

    // EFFECTS: Writes the given savable to the path associated with this
    //          json writer, passing the given arguments to the savable.
    public <T> void write(Savable<T> am, T args) throws IOException {
        JSONObject jso = am.toJson(args);
        Files.write(Paths.get(this.dstPath),
                jso.toString(1)
                        .getBytes(StandardCharsets.UTF_8));
    }

}
