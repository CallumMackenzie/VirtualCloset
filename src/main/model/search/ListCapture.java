package model.search;

import java.util.List;

// Captures a list of the given type from a sequence of characters.
public interface ListCapture<T> {
    // EFFECTS: Processes the next character in the input. Returns
    //          true if the list is finished, false otherwise.
    boolean isListFinished(char input);

    // EFFECTS: Returns the tokens captured to the list so far.
    List<T> getTokensCaptured();

    // EFFECTS: Returns the list separator string.
    String getListSeparatorString();

    // EFFECTS: Returns the list terminator string.
    String getListTerminatorString();
}
