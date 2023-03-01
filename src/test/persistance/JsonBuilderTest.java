package persistance;

import model.Outfit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonBuilderTest {

    @Test
    void testSavableSingle() {
        Outfit o = new Outfit("S", new ArrayList<>());
        JSONObject jso = new JsonBuilder()
                .savable("ABC", o, new ArrayList<>());
        assertNotNull(jso.get("ABC"));
        assertEquals("S", jso.getJSONObject("ABC").get(Outfit.JSON_NAME_KEY));
    }

    @Test
    void testSavableMultiple() {
        List<Outfit> os = new ArrayList<>();
        os.add(new Outfit("PQR", new ArrayList<>()));
        os.add(new Outfit("ST", new ArrayList<>()));
        JSONObject o = new JsonBuilder()
                .savable("sss", os, new ArrayList<>());
        assertNotNull(o.getJSONArray("sss"));
        assertEquals(2, o.getJSONArray("sss").length());
    }

    @Test
    void testSavableArray() {
        List<Outfit> os = new ArrayList<>();
        os.add(new Outfit("PQR", new ArrayList<>()));
        os.add(new Outfit("ST", new ArrayList<>()));
        JSONArray jsa = JsonBuilder.savableArray(os, new ArrayList<>());
        assertEquals(2, jsa.length());
    }

    @Test
    void testToStringList() {
        JSONArray jsa = new JSONArray();
        jsa.put("123");
        jsa.put("456");
        List<String> ls = JsonBuilder.toStringList(jsa);
        assertEquals(2, ls.size());
        assertTrue(ls.containsAll(Arrays.asList("123", "456")));
    }

}