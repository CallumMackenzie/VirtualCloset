package model.search;

// Captures an integer from a character by character input
public class IntegerCapture {

    private final WhitespaceConsumer whitespaceConsumer;
    private final KeyStringSearcher endTokenSearcher;
    private final StringBuilder token;
    private int captured;

    // REQUIRES: endToken must contain no numeric characters.
    // EFFECTS: Returns a new integer capture with the given end token.
    public IntegerCapture(String endToken) {
        this.whitespaceConsumer = new WhitespaceConsumer();
        this.endTokenSearcher = new KeyStringSearcher(endToken);
        this.token = new StringBuilder();
    }

    // MODIFIES: this
    // EFFECTS: Returns whether an integer has been captured.
    public boolean foundInteger(char input) throws NumberFormatException {
        if (whitespaceConsumer.shouldConsumeWhitespace(input)) {
            return false;
        }
        MatchState ms = this.endTokenSearcher.tryFindKey(input);
        if (ms.wasMatch()) {
            this.captured = Integer.parseInt(this.token.toString());
            return true;
        } else if (!Character.isDigit(input)
                && !ms.wasPartialMatch()) {
            throw new NumberFormatException("Expected a character input.");
        } else if (Character.isDigit(input)) {
            this.token.append(input);
        }
        return false;
    }

    // REQUIRES: this.foundInteger has returned true
    // EFFECTS: Returns the integer captured
    public int getIntegerCaptured() {
        return this.captured;
    }
}
