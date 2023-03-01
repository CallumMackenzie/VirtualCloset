package persistance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonReaderTest {

    JsonReader jsr;

    @BeforeEach
    void createJsr() {
        this.jsr = new JsonReader("./data/data.json");
    }

    @Test
    void testConstructor() {
        assertEquals("./data/data.json", this.jsr.srcPath);
    }

    @Test
    void testReadFile() {
        try {
            this.jsr = new JsonReader("/data/adskdasadnoskddskd");
            jsr.readFile();
            fail("Expected IOException.");
        } catch (IOException e) {
            // Expected
        }
    }

}