package persistance;

import model.Outfit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Test
    void testMapToIndexSorted() {
        List<Integer> ints = Arrays.asList(3, 4, 9, 100, 200);
        List<Integer> allInts = Arrays.asList(1, 2, 3, 4, 5, 6, 9, 12, 100, 200, 240);
        int[] mapped = JsonBuilder.mapToIndexSorted(ints, allInts).toArray();
        assertEquals(ints.size(), mapped.length);
        for (int i = 0; i < mapped.length; ++i) {
            assertEquals(mapped[i], allInts.indexOf(ints.get(i)));
        }
    }

    @Test
    void testMapToValueSorted() {
        List<Integer> ints = Arrays.asList(3, 4, 9, 100, 200);
        List<Integer> allInts = Arrays.asList(1, 2, 3, 4, 5, 6, 9, 12, 100, 200, 240);
        int[] mapped = {2, 3, 6, 8, 9};
        List<Integer> ints2 = JsonBuilder.mapToValueSorted(mapped, allInts)
                .collect(Collectors.toList());
        assertEquals(ints, ints2);
    }

}