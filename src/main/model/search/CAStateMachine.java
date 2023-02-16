package model.search;

import java.util.List;
import java.util.function.Consumer;

// A clothing address state machine for parsing a clothing address
// from a character-based input.
public class CAStateMachine
        extends StateMachine<CAStateMachine.State> {

    public static final String STYLE_CAPTURE_STR = "style";
    public static final String BRAND_CAPTURE_STR = "brand";
    public static final String EQUALITY_STR = "=";
    public static final String LIST_SEPARATOR_STR = ",";
    public static final String LIST_END_STR = ";";

    // EFFECTS: Retrieves the next state given the current internal
    //          state.
    @Override
    public State nextState(State in, char input) {
        return in.process(input);
    }

    // EFFECTS: Creates a new clothing address state machine
    //          with default initial state.
    public CAStateMachine() {
        // Dummy state to copy from, kinda hacky but avoids adding
        // a default constructor which may be accidentally invoked
        // implicitly instead of the previous state based one which
        // would be an annoying bug.
        super(new CapturingState(new State(new ClothingAddress()) {
            @Override
            public State process(char input) {
                return null;
            }
        }));
    }

    // An abstract state representation for the clothing address
    // state machine with an internal clothing address to modify
    // as information entered is parsed.
    public abstract static class State {

        private final ClothingAddress address;

        // EFFECTS: Creates a new state with the given clothing address
        public State(ClothingAddress address) {
            this.address = address;
        }

        // EFFECTS: Creates a new state from the previous
        public State(State base) {
            this.address = base.address;
        }

        // EFFECTS: Returns the clothing address constructed by
        //          this state.
        public ClothingAddress getAddress() {
            return this.address;
        }

        // MODIFIES: this
        // EFFECTS: Processes the given input, producing a new state.
        public abstract State process(char input);
    }

    // A state which parses outer filter items such as brand, size, etc
    public static class CapturingState extends State {

        private final StringBuilder captured;
        private final KeyStringSearcher equalityStrSearcher;
        private final WhitespaceConsumer whitespaceConsumer;

        // EFFECTS: Creates a new capturing state
        public CapturingState(State last) {
            super(last);
            this.captured = new StringBuilder();
            this.whitespaceConsumer = new WhitespaceConsumer();
            this.equalityStrSearcher = new KeyStringSearcher(EQUALITY_STR,
                    this.captured::append);
        }

        // MODIFIES: this
        // EFFECTS: Consumes leading whitespace if present, then
        //          parses input preceding EQUALITY_STR as the key
        //          to dictate the next state.
        @Override
        public State process(char input) {
            // Consume leading whitespace
            if (this.whitespaceConsumer.shouldConsumeWhitespace(input)) {
                return this;
            }
            KeyStringSearcher.MatchState equalityTokenMatch
                    = this.equalityStrSearcher.tryFindKey(input);
            if (equalityTokenMatch == KeyStringSearcher.MatchState.MATCH) {
                return this.nextStateFromCaptured();
            } else if (equalityTokenMatch == KeyStringSearcher.MatchState.NO_MATCH) {
                captured.append(input);
            }
            return this;
        }

        // EFFECTS: Returns the next state dependent on the current
        //          string input. If this input is not found, returns
        //          null.
        private State nextStateFromCaptured() {
            switch (captured.toString()
                    .toLowerCase().trim()) {
                case BRAND_CAPTURE_STR:
                    return new StringListCaptureState(this,
                            getAddress().getBrands()::addAll);
                case STYLE_CAPTURE_STR:
                    return new StringListCaptureState(this,
                            getAddress().getStyles()::addAll);
                // TODO
                default:
                    return null;
            }
        }
    }

    // TODO
    public static class StringListCaptureState extends State {

        private final StringListCapture listCapture;
        private final Consumer<List<String>> onCapture;

        // TODO
        public StringListCaptureState(State last,
                                      Consumer<List<String>> onCapture) {
            super(last);
            this.onCapture = onCapture;
            this.listCapture = new StringListCapture(
                    LIST_SEPARATOR_STR,
                    LIST_END_STR
            );
        }

        // TODO
        @Override
        public State process(char input) {
            if (listCapture.isListFinished(input)) {
                this.onCapture.accept(listCapture.getTokensCaptured());
                return new CapturingState(this);
            }
            return this;
        }
    }

}
