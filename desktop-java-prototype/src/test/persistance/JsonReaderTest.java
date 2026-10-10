package persistance;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonReaderTest {

    JsonReader jsr;

    @BeforeEach
    void createJsr() {
        this.jsr = new JsonReader("./data/readtestdata.json");
    }

    @Test
    void testConstructor() {
        assertEquals("./data/readtestdata.json", this.jsr.srcPath);
    }

    @Test
    void testReadFileThrows() {
        try {
            this.jsr = new JsonReader("/data/adskdasadnoskddskd");
            jsr.readFile();
            fail("Expected IOException.");
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    void testReadFile() {
        try {
            JSONObject jso = this.jsr.readFileJson();
            assertEquals(jso.getString("test-key"), "test val");
        } catch (IOException e) {
            fail(e);
        }
    }

}