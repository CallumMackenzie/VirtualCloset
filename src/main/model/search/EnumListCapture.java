package model.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Processes char by char input into a list of enum constants of the given class
public class EnumListCapture<T extends Enum<T>> implements ListCapture<T> {

    private final StringListCapture stringListCapture;
    private final boolean matchStrict;
    private final Class<T> enumClass;
    private final List<T> tokens;

    // EFFECTS: Creates a new enum list capture with the given list separator
    //          and list terminator. The matchStrict parameter indicates whether the
    //          enum names need to be matched exactly or simply roughly in terms of case
    //          and symbol separation (spaces versus underscores).
    public EnumListCapture(boolean matchStrict,
                           Class<T> enumClass,
                           String listSeparatorStr,
                           String listEndStr) {
        this.stringListCapture = new StringListCapture(listSeparatorStr,
                listEndStr);
        this.matchStrict = matchStrict;
        this.tokens = new ArrayList<>();
        this.enumClass = enumClass;
    }

    // MODIFIES: this
    // EFFECTS: Processes the next char in the input, returning true if the
    //          list has completed.
    @Override
    public boolean isListFinished(char input) {
        boolean finished = this.stringListCapture.isListFinished(input);
        List<String> stringTokens = this.stringListCapture.getTokensCaptured();
        if (stringTokens.size() != this.tokens.size()
                && !stringTokens.isEmpty()) {
            String latestToken = stringTokens.get(stringTokens.size() - 1);
            this.tokens.add(this.matchStrict
                    ? this.stringToEnumStrict(latestToken)
                    : this.stringToEnumLoose(latestToken));
        }
        return finished;
    }

    // EFFECTS: Returns the matching enum name based on the given string,
    //          or null if it does not match any. Matches must be exact
    //          to case and order.
    private T stringToEnumStrict(String in) {
        return Arrays.stream(this.enumClass.getEnumConstants())
                .filter(t -> t.name().equals(in))
                .findFirst()
                .orElse(null);
    }

    // EFFECTS: Returns the matching enum name based on the given string,
    //          or null if it does not match any. Matches may have different
    //          case and whitespace. The first match is returned.
    private T stringToEnumLoose(String in) {
        String formatted = in.replaceAll("\\s+", "_");
        return Arrays.stream(this.enumClass.getEnumConstants())
                .filter(t -> t.name()
                        .equalsIgnoreCase(formatted))
                .findFirst()
                .orElse(null);
    }

    // EFFECTS: Returns the tokens captured by this list capture so far.
    @Override
    public List<T> getTokensCaptured() {
        return this.tokens;
    }

    // EFFECTS: Returns the list separator string this list capture is matching.
    @Override
    public String getListSeparatorString() {
        return this.stringListCapture.getListSeparatorString();
    }

    // EFFECTS: Returns the list terminator string this list capture is matching.
    @Override
    public String getListTerminatorString() {
        return this.stringListCapture.getListTerminatorString();
    }
}
