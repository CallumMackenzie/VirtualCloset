package persistance;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    JsonWriter jsw;

    @BeforeEach
    void createJsw() {
        this.jsw = new JsonWriter("./data/testdata.json");
    }

    @Test
    void testConstructor() {
        assertEquals("./data/testdata.json", this.jsw.dstPath);
    }

    @Test
    void testWrite() {
        Savable<Void> s = args -> new JSONObject()
                .put("a", "b");
        assertDoesNotThrow(() -> this.jsw.write(s, null));
        JsonReader jsr = new JsonReader(this.jsw.dstPath);
        try {
            JSONObject jso = jsr.readFileJson();
            assertEquals(jso.getString("a"), "b");
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void testWriteWithArgs() {
        Savable<String> s = args -> new JSONObject()
                .put("abc", args);
        assertDoesNotThrow(() -> this.jsw.write(s, "pqr"));
        JsonReader jsr = new JsonReader(this.jsw.dstPath);
        try {
            JSONObject jso = jsr.readFileJson();
            assertEquals(jso.getString("abc"), "pqr");
        } catch (IOException e) {
            fail(e);
        }
    }

}