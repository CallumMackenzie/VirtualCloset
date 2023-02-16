package model.search;

import java.util.ArrayList;
import java.util.List;

// Captures a list of strings from character-by-character input
public class StringListCapture {

    private final WhitespaceConsumer whitespaceConsumer;
    private final KeyStringSearcher listSeparatorSearcher;
    private final KeyStringSearcher listEndSearcher;
    private final List<String> tokensCaptured;
    private final StringBuilder currentTokenCapture;

    // EFFECTS: Sets up for list capturing
    public StringListCapture(String listSeparatorStr,
                             String listEndStr) {
        this.currentTokenCapture = new StringBuilder();
        this.whitespaceConsumer = new WhitespaceConsumer();
        this.tokensCaptured = new ArrayList<>(5);
        this.listSeparatorSearcher = new KeyStringSearcher(listSeparatorStr,
                this.currentTokenCapture::append);
        this.listEndSearcher = new KeyStringSearcher(listEndStr,
                this.currentTokenCapture::append);
    }

    // EFFECTS: Returns the list separator string
    public String getListSeparatorString() {
        return this.listSeparatorSearcher.getKey();
    }

    // EFFECTS: Returns the list end string
    public String getListTerminatorString() {
        return this.listEndSearcher.getKey();
    }

    // EFFECTS: Returns the tokens captured
    public List<String> getTokensCaptured() {
        return this.tokensCaptured;
    }

    // MODIFIES: this
    // EFFECTS: Returns true if the list has finished building,
    //          false otherwise.
    public boolean isListFinished(char input) {
        if (this.whitespaceConsumer.shouldConsumeWhitespace(input)) {
            return false;
        }
        KeyStringSearcher.MatchState listEndMatch = this.listEndSearcher.tryFindKey(input);
        KeyStringSearcher.MatchState listSepMatch = this.listSeparatorSearcher.tryFindKey(input);
        if (listEndMatch.wasMatch() || listSepMatch.wasMatch()) {
            String tokenStr = this.currentTokenCapture.toString().trim();
            if (tokenStr.length() != 0) {
                this.tokensCaptured.add(tokenStr);
            }
            if (listSepMatch.wasMatch()) {
                this.currentTokenCapture.setLength(0);
                this.whitespaceConsumer.reset();
                this.listSeparatorSearcher.reset();
            }
            return listEndMatch.wasMatch();
        }
        if (listSepMatch == KeyStringSearcher.MatchState.NO_MATCH
                && listEndMatch == KeyStringSearcher.MatchState.NO_MATCH) {
            currentTokenCapture.append(input);
        }
        return false;
    }
}
