package model.search;

// Consumes whitespace before any token
class WhitespaceConsumer {
    private boolean startToken;

    // EFFECTS: Creates a new whitespace consumer which consumes
    //          leading whitespace.
    public WhitespaceConsumer() {
        this.reset();
    }

    // MODIFIES: this
    // EFFECTS: Resets this whitespace consumer
    public void reset() {
        this.startToken = false;
    }

    // MODIFIES: this
    // EFFECTS: Returns whether to consume the given char
    //          as pre-token whitespace.
    public boolean shouldConsumeWhitespace(char input) {
        if (!this.startToken) {
            if (Character.isWhitespace(input)) {
                return true;
            } else {
                this.startToken = true;
            }
        }
        return false;
    }

    // EFFECTS: Returns whether this is still consuming whitespace
    public boolean isConsuming() {
        return !this.startToken;
    }
}
