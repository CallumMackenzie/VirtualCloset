package persistance;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Reads in a JSON file from the given path and parses it into internal application state.
public class JsonReader {

    public final String srcPath;

    // EFFECTS: Creates a new reader to read from the given path.
    public JsonReader(String srcPath) {
        this.srcPath = srcPath;
    }

    // EFFECTS: Reads the file at srcPath and returns it as a string
    public String readFile() throws IOException {
        Path p = Paths.get(this.srcPath);
        return new String(Files.readAllBytes(p));
    }

    // EFFECTS: Reads the given file to a JSONObject
    public JSONObject readFileJson() throws IOException {
        String fileData = this.readFile();
        return new JSONObject(fileData);
    }

}
