package persistance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// A JSONObject wrapper for Savable objects
public class JsonBuilder extends JSONObject {

    // MODIFIES: this
    // EFFECTS: Adds the given savable at the given key
    public <T> JsonBuilder savable(String key, Savable<T> s, T args) {
        this.put(key, s.toJson(args));
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Adds the given savable list to this JSONObject at the given key
    public <T extends Savable<E>, E> JsonBuilder savable(String key,
                                                         Iterable<T> s,
                                                         E args) {
        this.put(key, savableArray(s, args));
        return this;
    }

    // EFFECTS: Returns a JSONArray of the given Savable
    public static <T extends Savable<E>, E>
    JSONArray savableArray(Iterable<T> s, E args) {
        JSONArray jsa = new JSONArray();
        for (Savable<E> savable : s) {
            jsa.put(savable.toJson(args));
        }
        return jsa;
    }

    // EFFECTS: Returns the given JSONArray as a mutable string list
    public static List<String> toStringList(JSONArray jsa) {
        return jsa.toList().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    // REQUIRES: allTs is sorted and contains every element in ts
    // EFFECTS: Maps each t in ts to its index in allTs
    public static <T extends Comparable<T>>
    IntStream mapToIndexSorted(List<T> ts, List<T> allTs) {
        return ts.stream().mapToInt(t -> Collections.binarySearch(
                allTs, t
        ));
    }

    // REQUIRES: allTs is sorted. ts contains indexes from allTs
    // EFFECTS: Maps each index to its value in allTs
    public static <T extends Comparable<T>>
    Stream<T> mapToValueSorted(int[] idxs, List<T> allTs) {
        return IntStream.of(idxs).mapToObj(allTs::get);
    }
}
