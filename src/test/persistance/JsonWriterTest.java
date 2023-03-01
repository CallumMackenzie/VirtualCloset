package persistance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonWriterTest {

    JsonWriter jsw;

    @BeforeEach
    void createJsw() {
        this.jsw = new JsonWriter("./data/data.json");
    }

    @Test
    void testConstructor() {
        assertEquals("./data/data.json", this.jsw.dstPath);
    }

}