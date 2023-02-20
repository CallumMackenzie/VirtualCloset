package model.search;

// Captures a boolean value from a char input
public class BooleanCapture {

    private final KeyStringSearcher trueSearcher;
    private final KeyStringSearcher falseSearcher;
    private final WhitespaceConsumer whitespaceConsumer;
    private boolean hasFoundBool;
    private boolean boolCaptured;

    // REQUIRES: trueKey != falseKey
    // EFFECTS: Creates a new boolean capture object to find a boolean
    //          matching either the true key or false key, while consuming
    //          leading whitespace.
    public BooleanCapture(String trueKey,
                          String falseKey) {
        this.hasFoundBool = false;
        this.whitespaceConsumer = new WhitespaceConsumer();
        this.trueSearcher = new KeyStringSearcher(trueKey,
                c -> {
                });
        this.falseSearcher = new KeyStringSearcher(falseKey,
                x -> {
                });
    }

    // MODIFIES: this
    // EFFECTS: Processes the full input stream of characters,
    //          returning true if a boolean was found.
    public boolean foundBoolean(String in) throws UnexpectedInputException {
        for (char c : in.toCharArray()) {
            if (this.foundBoolean(c)) {
                return true;
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: Parses the char input and checks if there has been a match
    //          to a true/false key, returning true if this is the case.
    public boolean foundBoolean(char input) throws UnexpectedInputException {
        if (!this.hasFoundBool
                && !this.whitespaceConsumer.shouldConsumeWhitespace(input)) {
            KeyStringSearcher.MatchState foundTrue = this.trueSearcher.tryFindKey(input);
            KeyStringSearcher.MatchState foundFalse = this.falseSearcher.tryFindKey(input);
            if (foundTrue.wasMatch() || foundFalse.wasMatch()) {
                hasFoundBool = true;
                // Assume foundTrue and foundFalse are not both true
                // due to constructor precondition.
                this.boolCaptured = foundTrue.wasMatch();
            } else if (foundTrue.wasNoMatch()
                    && foundFalse.wasNoMatch()) {
                throw new UnexpectedInputException("Expected a boolean value of \""
                        + this.getTrueKey() + "\" or \""
                        + this.getFalseKey() + "\".");
            }
        }
        return this.hasFoundBool;
    }

    // EFFECTS: Returns the true key
    public String getTrueKey() {
        return this.trueSearcher.getKey();
    }

    // EFFECTS: Returns the false key
    public String getFalseKey() {
        return this.falseSearcher.getKey();
    }

    // EFFECTS: Returns the boolean captured from the char input
    //          if it has been found, otherwise there is no guarantee
    //          on the value.
    public boolean getBoolCaptured() {
        return this.boolCaptured;
    }

}
