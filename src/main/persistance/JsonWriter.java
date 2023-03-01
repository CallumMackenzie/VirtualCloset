package persistance;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// Serializes application data to a JSON format at the given path
public class JsonWriter {

    public final String dstPath;

    public JsonWriter(String path) {
        this.dstPath = path;
    }

    // TODO: Test this
    public <T> void write(Savable<T> am, T args) throws IOException {
        JSONObject jso = am.toJson(args);
        Files.write(Paths.get(this.dstPath),
                jso.toString(1)
                        .getBytes(StandardCharsets.UTF_8));
    }

}
