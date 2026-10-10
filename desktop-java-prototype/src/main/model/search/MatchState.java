package model.search;

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
