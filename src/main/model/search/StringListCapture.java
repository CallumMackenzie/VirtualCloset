package model.search;

import model.search.KeyStringSearcher.MatchState;

import java.util.ArrayList;
import java.util.List;

// Captures a list of strings from character-by-character input
public class StringListCapture implements ListCapture<String> {

    private final WhitespaceConsumer whitespaceConsumer;
    private final KeyStringSearcher listSepSearcher;
    private final KeyStringSearcher listEndSearcher;
    private final List<String> tokensCaptured;
    private final StringBuilder currentTokenCapture;

    // REQUIRES: Keys cannot contain each other; must be unique.
    // EFFECTS: Sets up for list capturing
    public StringListCapture(String listSeparatorStr,
                             String listEndStr) {
        this.currentTokenCapture = new StringBuilder();
        this.whitespaceConsumer = new WhitespaceConsumer();
        this.tokensCaptured = new ArrayList<>(5);
        this.listSepSearcher = new KeyStringSearcher(listSeparatorStr);
        this.listEndSearcher = new KeyStringSearcher(listEndStr);
    }

    // EFFECTS: Returns the list separator string
    @Override
    public String getListSeparatorString() {
        return this.listSepSearcher.getKey();
    }

    // EFFECTS: Returns the list end string
    @Override
    public String getListTerminatorString() {
        return this.listEndSearcher.getKey();
    }

    // EFFECTS: Returns the tokens captured
    @Override
    public List<String> getTokensCaptured() {
        return this.tokensCaptured;
    }

    // MODIFIES: this
    // EFFECTS: Returns true if the list has finished building,
    //          false otherwise.
    @Override
    public boolean isListFinished(char input) {
        if (this.whitespaceConsumer.shouldConsumeWhitespace(input)) {
            return false;
        }
        MatchState listEndMatch = this.listEndSearcher.tryFindKey(input);
        MatchState listSepMatch = this.listSepSearcher.tryFindKey(input);
        this.processMatchStates(listSepMatch, listEndMatch, input);

        return listEndMatch.wasMatch();
    }

    // REQUIRES: the list separator token has just been matched
    // MODIFIES: this
    // EFFECTS: Performs the match procedure for list separator
    private void registerListSepMatch() {
        this.reclaimListEndCapture();
        this.captureCurrentToken();
        this.currentTokenCapture.setLength(0);
        this.whitespaceConsumer.reset();
        this.listSepSearcher.reset();
        this.listEndSearcher.reset();
    }

    // REQUIRES: the list end token has just been matched
    // MODIFIES: this
    // EFFECTS: Performs the match procedure for list end
    private void registerListEndMatch() {
        this.reclaimListSepCapture();
        this.captureCurrentToken();
    }

    // REQUIRES: there is a valid token in this.currentTokenCapture
    // MODIFIES: this
    // EFFECTS: Appends the string in this.currentTokenCapture to the list of
    //          tokens if it is not empty.
    private void captureCurrentToken() {
        String tokenStr = this.currentTokenCapture.toString().trim();
        if (tokenStr.length() != 0) {
            this.tokensCaptured.add(tokenStr);
        }
    }

    // MODIFIES: this
    // EFFECTS: Returns the partial match of reclaim while taking into account
    //          any current partial matches of other to avoid conflict.
    public static String reclaimListCapture(KeyStringSearcher reclaim,
                                            KeyStringSearcher other) {
        String partial = reclaim.getPartialMatch();
        int reclaimKeyLen = reclaim.getKey().length();
        int otherKeyLen = other.getKey().length();
        int otherKeyIdx = other.getCurrentKeyIndex();
        int keyLengthDiff = reclaimKeyLen - otherKeyLen
                - otherKeyIdx - 1
                + partial.length();
        if (keyLengthDiff < 0) {
            return "";
        }
        if (keyLengthDiff <= partial.length()) {
            return partial.substring(0, keyLengthDiff);
        }
        return partial;
    }

    // MODIFIES: this
    // EFFECTS: Takes the longer value to reclaim
    private void takeLongerReclaim() {
        String rec1 = this.listSepSearcher.getPartialMatch();
        String rec2 = this.listEndSearcher.getPartialMatch();
        this.appendInput(rec1.length() > rec2.length()
                ? rec1 : rec2);
    }

    // MODIFIES: this
    // EFFECTS: Appends the list separator partial match to the current token
    private void reclaimListSepCapture() {
        String reclaimed = reclaimListCapture(this.listSepSearcher,
                this.listEndSearcher);
        this.appendInput(reclaimed);
    }

    // MODIFIES: this
    // EFFECTS: Appends the list end partial match to the current token
    private void reclaimListEndCapture() {
        String reclaimed = reclaimListCapture(this.listEndSearcher,
                this.listSepSearcher);
        this.appendInput(reclaimed);
    }

    // MODIFIES: this
    // EFFECTS: Checks if the current match state should append the input
    //          value, and appends values as needed.
    private void processMatchStates(MatchState sep,
                                    MatchState end,
                                    char input) {
        Runnable appendSingle = () -> this.appendInput(input);
        // No match, partial, match, broken, restarted
        Runnable[][][] actionMatrix = getActionMatrix(appendSingle);
        for (Runnable action : actionMatrix[sep.ordinal()][end.ordinal()]) {
            action.run();
        }
    }

    // MODIFIES: this
    // EFFECTS: Appends the given character to the current token capture
    //          for this object.
    private void appendInput(char input) {
        this.currentTokenCapture.append(input);
    }

    // MODIFIES: this
    // EFFECTS: Appends the given string to the current token capture
    //          for this object.
    private void appendInput(String input) {
        this.currentTokenCapture.append(input);
    }

    // EFFECTS: Returns an action matrix for list sep no match
    private Runnable[][] sepNoMatchMatrix(Runnable appendSingle) {
        return new Runnable[][]{
                {appendSingle}, // No match list end
                {}, // Partial list end
                {this::registerListEndMatch}, // Match list end
                {this::reclaimListEndCapture, appendSingle}, // Broken list end
                {this::reclaimListEndCapture} // Restarted list end
        };
    }

    // EFFECTS: Returns an action matrix for list sep partial match
    private Runnable[][] sepPartialMatchMatrix() {
        return new Runnable[][]{
                {}, //  No match list end
                {}, // Partial list end
                {this::registerListEndMatch}, // Match list end
                {this::reclaimListEndCapture}, // Broken list end
                {this::reclaimListEndCapture} // Restarted list end
        };
    }

    // EFFECTS: Returns an action matrix for list sep match
    private Runnable[][] sepMatchMatrix() {
        return new Runnable[][]{
                {this::registerListSepMatch}, //  No match list end
                {this::registerListSepMatch}, // Partial list end
                {}, // Match list end
                {this::registerListSepMatch}, // Broken list end
                {this::registerListSepMatch} // Restarted list end
        };
    }

    // EFFECTS: Returns an action matrix for list sep match broken
    private Runnable[][] sepMatchBrokenMatrix(Runnable appendSingle) {
        return new Runnable[][]{
                {this::reclaimListSepCapture, appendSingle}, // No match list end
                {this::reclaimListSepCapture}, // Partial list end
                {this::registerListEndMatch}, // Match list end
                {this::takeLongerReclaim, appendSingle}, // Broken list end
                {this::takeLongerReclaim} // Restarted list end
        };
    }

    // EFFECTS: Returns an action matrix for list sep match restarted
    private Runnable[][] sepMatchRestartedMatrix() {
        return new Runnable[][]{
                {this::reclaimListSepCapture}, // No match list end
                {this::reclaimListSepCapture}, // Partial list end
                {this::registerListEndMatch}, // Match list end
                {this::takeLongerReclaim}, // Broken list end
                {this::takeLongerReclaim} // Restarted list end
        };
    }

    // EFFECTS: Returns an action matrix for the given input
    private Runnable[][][] getActionMatrix(Runnable appendSingle) {
        return new Runnable[][][]{
                this.sepNoMatchMatrix(appendSingle),
                this.sepPartialMatchMatrix(),
                this.sepMatchMatrix(),
                this.sepMatchBrokenMatrix(appendSingle),
                this.sepMatchRestartedMatrix()
        };
    }
}
