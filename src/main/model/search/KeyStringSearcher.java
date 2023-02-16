package model.search;

import java.util.function.Consumer;

// Allows quantized searching for a given key string, capturing input
// to the provided onNoMatch function.
class KeyStringSearcher {

    // Represents match states for a given quantized input.
    public enum MatchState {
        // No match, or match failed
        NO_MATCH,
        // Possible match depending on upcoming input
        PARTIAL_MATCH,
        // Match
        MATCH;

        // EFFECTS: Returns true if this is MATCH
        public boolean wasMatch() {
            return this == MATCH;
        }
    }

    private final String key;
    private int currentKeyIndex;
    private final Consumer<String> onNoMatch;

    // REQUIRES: Key cannot match a pattern deeply which needs to be
    //           captured elsewhere.
    // EFFECTS: Creates a new KeyStringSearcher to search for the given key,
    //          capturing to the no match function when a match is terminated
    //          partway through.
    public KeyStringSearcher(String key, Consumer<String> onNoMatch) {
        this.key = key;
        this.onNoMatch = onNoMatch;
        this.reset();
    }

    // EFFECT: Returns the key this searcher is looking for
    public String getKey() {
        return this.key;
    }

    // EFFECT: Returns whether this searcher is in the process of matching
    public boolean isMatching() {
        return this.currentKeyIndex != -1;
    }

    // MODIFIES: this
    // EFFECTS: Resets the internal state to search for another
    //          key.
    public void reset() {
        this.currentKeyIndex = -1;
    }

    // MODIFIES: this
    // EFFECTS: Captures input if it is matching the given key string
    //          so far. Once a full match is made, returns MatchState.MATCH
    //          every call. If a partial match is broken, appends captured characters
    //          to the StringBuilder associated with this object and returns
    //          MatchState.PARTIAL_MATCH. Otherwise, returns MatchState.NO_MATCH.
    public MatchState tryFindKey(char input) {
        // Is the current index long enough for the match?
        boolean partial = false;
        if (currentKeyIndex + 1 < key.length()) {
            // Character matches?
            if (input == key.charAt(currentKeyIndex + 1)) {
                currentKeyIndex += 1;
                partial = true;
            } else if (currentKeyIndex != -1) { // Partial match no longer valid
                this.onNoMatch.accept(key.substring(0, currentKeyIndex + 1));
                // Check for start of match again
                if (input == key.charAt(0)) {
                    currentKeyIndex = 0;
                    partial = true;
                } else {
                    currentKeyIndex = -1;
                }
            }
        }
        boolean fullMatch = currentKeyIndex + 1 == key.length();
        if (fullMatch) {
            return MatchState.MATCH;
        } else if (partial) {
            return MatchState.PARTIAL_MATCH;
        } else {
            return MatchState.NO_MATCH;
        }
    }
}
