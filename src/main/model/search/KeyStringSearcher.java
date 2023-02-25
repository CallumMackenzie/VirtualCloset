package model.search;

// Allows quantized searching for a given key string, capturing input
// to the provided onNoMatch function.
class KeyStringSearcher {

    private final String key;
    private String partialMatchCaptured;
    private int currentKeyIndex;

    // REQUIRES: Key cannot match a pattern deeply which needs to be
    //           captured elsewhere. Key cannot be empty.
    // EFFECTS: Creates a new KeyStringSearcher to search for the given key.
    public KeyStringSearcher(String key) {
        this.key = key;
        this.partialMatchCaptured = "";
        this.reset();
    }

    // REQUIRES: Last result of tryFindKey indicated a partial, broken,
    //           or restarted match.
    // EFFECTS: Returns the partial match cancelled this round, not including
    //          the character which caused the non-match.
    public String getPartialMatch() {
        return this.partialMatchCaptured;
    }

    // EFFECT: Returns the key this searcher is looking for
    public String getKey() {
        return this.key;
    }

    // EFFECT: Returns whether this searcher is in the process of matching
    public boolean isMatching() {
        return this.currentKeyIndex != -1;
    }

    // EFFECT: Returns the index of the chars matched up to on the key,
    //          or -1 if there are none.
    public int getCurrentKeyIndex() {
        return this.currentKeyIndex;
    }

    // MODIFIES: this
    // EFFECTS: Resets the internal state to search for another
    //          key, and sets the partial match captured to an
    //          empty string.
    public void reset() {
        this.currentKeyIndex = -1;
        this.partialMatchCaptured = "";
    }

    // MODIFIES: this
    // EFFECTS: Captures input if it is matching the given key string
    //          so far. Once a full match is made, returns MatchState.MATCH
    //          every call. If a partial match is broken, appends captured characters
    //          to the StringBuilder associated with this object and returns
    //          MatchState.PARTIAL_MATCH. Otherwise, returns MatchState.NO_MATCH.
    public MatchState tryFindKey(char input) {
        // Is the current index long enough for the match?
        MatchState result = MatchState.NO_MATCH;
        if (currentKeyIndex + 1 < key.length()) {
            // Character matches?
            if (input == key.charAt(currentKeyIndex + 1)) {
                currentKeyIndex += 1;
                result = MatchState.PARTIAL_MATCH;
                this.partialMatchCaptured = key.substring(0, currentKeyIndex + 1);
            } else if (currentKeyIndex != -1) { // Partial match no longer valid
                result = MatchState.MATCH_BROKEN;
                this.partialMatchCaptured = key.substring(0, currentKeyIndex + 1);
                // Check for start of match again
                if (input == key.charAt(0)) {
                    currentKeyIndex = 0;
                    result = MatchState.MATCH_RESTARTED;
                } else {
                    currentKeyIndex = -1;
                }
            }
        }
        boolean fullMatch = currentKeyIndex + 1 == key.length();
        if (fullMatch) {
            return MatchState.MATCH;
        }
        return result;
    }


    // Represents match states for a given quantized input.
    public enum MatchState {
        // No match, or match failed
        NO_MATCH,
        // Possible match depending on upcoming input
        PARTIAL_MATCH,
        // Match
        MATCH,
        // Partial match just broken
        MATCH_BROKEN,
        // Partial match just broken, but is partial again
        MATCH_RESTARTED;

        // EFFECTS: Returns true if this is MATCH
        public boolean wasMatch() {
            return this == MATCH;
        }

        // EFFECTS: Returns true if this is PARTIAL_MATCH
        public boolean wasPartialMatch() {
            return this == PARTIAL_MATCH;
        }

        // EFFECTS: Returns true if this is NO_MATCH
        public boolean wasNoMatch() {
            return this == NO_MATCH;
        }

        // EFFECTS: Returns true if this is PARTIAL_MATCH_BROKEN
        public boolean wasMatchBroken() {
            return this == MATCH_BROKEN;
        }

        // EFFECTS: Returns true if this is PARTIAL_MATCH_RESTARTED
        public boolean wasMatchRestarted() {
            return this == MATCH_RESTARTED;
        }

        // EFFECTS: Returns true if the value indicates a match
        //          error took place. Ie. NO_MATCH, MATCH_BROKEN,
        //          MATCH_RESTARTED.
        public boolean matchError() {
            return this.wasNoMatch()
                    || this.wasMatchRestarted()
                    || this.wasMatchBroken();
        }
    }
}
