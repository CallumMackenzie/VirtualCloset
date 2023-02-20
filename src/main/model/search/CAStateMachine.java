package model.search;

import model.Size;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// A clothing address state machine for parsing a clothing address
// from a character-based input.
public class CAStateMachine
        extends StateMachine<CAStateMachine.State, ClothingAddressParseException> {

    public static final String STYLE_CAPTURE_STR = "style";
    public static final String BRAND_CAPTURE_STR = "brand";
    public static final String TYPE_CAPTURE_STR = "type";
    public static final String SIZE_CAPTURE_STR = "size";
    public static final String IS_DIRTY_CAPTURE_STR = "dirty";

    public static final String TRUE_STR = "yes";
    public static final String FALSE_STR = "no";
    public static final String EQUALITY_STR = "=";
    public static final String LIST_SEPARATOR_STR = ",";
    public static final String LIST_END_STR = ";";

    // EFFECTS: Retrieves the next state given the current internal
    //          state.
    @Override
    public State nextState(State in, char input) throws ClothingAddressParseException {
        return in.process(input);
    }

    // EFFECTS: Creates a new clothing address state machine
    //          with default initial state.
    public CAStateMachine() {
        super(null);
        this.setState(new CapturingState());
    }

    // An abstract state representation for the clothing address
    // state machine with an internal clothing address to modify
    // as information entered is parsed.
    public abstract class State {

        private final ClothingAddress address;

        // EFFECTS: Creates a new state from the current. If the previous
        //          base has no clothing address (null value), creates a new
        //          one for this state.
        public State() {
            if (getState() == null) {
                this.address = new ClothingAddress();
            } else {
                this.address = Objects.requireNonNullElseGet(getState().address,
                        ClothingAddress::new);
            }
        }

        // EFFECTS: Returns the clothing address constructed by
        //          this state.
        public ClothingAddress getAddress() {
            return this.address;
        }

        // MODIFIES: this
        // EFFECTS: Processes the given input, producing a new state.
        public abstract State process(char input)
                throws ClothingAddressParseException;
    }

    // A state which parses outer filter items such as brand, size, etc
    public class CapturingState extends State {

        private final StringBuilder captured;
        private final KeyStringSearcher equalityStrSearcher;
        private final WhitespaceConsumer whitespaceConsumer;

        // EFFECTS: Creates a new capturing state with the last state
        //          as the current one in this state machine.
        public CapturingState() {
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
        public State process(char input) throws ClothingAddressParseException {
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
        private State nextStateFromCaptured() throws ClothingAddressParseException {
            String key = captured.toString().toLowerCase().trim();
            switch (key) {
                case BRAND_CAPTURE_STR:
                    return new StringListCaptureState(
                            getAddress().getBrands()::addAll);
                case STYLE_CAPTURE_STR:
                    return new StringListCaptureState(
                            getAddress().getStyles()::addAll);
                case TYPE_CAPTURE_STR:
                    return new StringListCaptureState(
                            getAddress().getTypes()::addAll);
                case SIZE_CAPTURE_STR:
                    return new EnumListCaptureState<>(Size.class,
                            getAddress().getSizes()::addAll);
                case IS_DIRTY_CAPTURE_STR:
                    return new BooleanCaptureState(
                            getAddress()::setIsDirty);
                // TODO
                default:
                    throw new NoSuchKeyException(key);
            }
        }
    }

    // Captures a list of strings, passing the completed list to the
    // user-provided consumer when it has been fully completed.
    public class StringListCaptureState extends State {

        private final StringListCapture listCapture;
        private final Consumer<List<String>> onCapture;

        // EFFECTS: Constructs a new string list capture state from the current
        //          state in this machine and the given string list consumer.
        public StringListCaptureState(Consumer<List<String>> onCapture) {
            this.onCapture = onCapture;
            this.listCapture = new StringListCapture(
                    LIST_SEPARATOR_STR,
                    LIST_END_STR
            );
        }

        // MODIFIES: this
        // EFFECTS: Processes the given character in the state machine
        //          to build the string list.
        @Override
        public State process(char input) {
            if (listCapture.isListFinished(input)) {
                this.onCapture.accept(listCapture.getTokensCaptured());
                return new CapturingState();
            }
            return this;
        }
    }

    // Captures a list of enum values, passing the fully completed
    // list to the user-provided consumer when it has been completed.
    public class EnumListCaptureState<T extends Enum<T>> extends State {

        private final EnumListCapture<T> enumListCapture;
        private final Consumer<List<T>> onCapture;

        // EFFECTS: Creates a new enum list capture state from the given
        //          enum class and capture function. Previous state data is
        //          pulled from the current state of this machine.
        public EnumListCaptureState(Class<T> enumClass,
                                    Consumer<List<T>> onCapture) {
            this.onCapture = onCapture;
            this.enumListCapture = new EnumListCapture<>(false,
                    enumClass,
                    LIST_SEPARATOR_STR,
                    LIST_END_STR);
        }

        // MODIFIES: this
        // EFFECTS: Processes the next character and returns the next state.
        @Override
        public State process(char input) {
            if (this.enumListCapture.isListFinished(input)) {
                this.onCapture.accept(this.enumListCapture.getTokensCaptured());
                return new CapturingState();
            }
            return this;
        }
    }

    // Captures a single boolean value, providing it to the consumer when
    // it is captured.
    public class BooleanCaptureState extends State {

        private final Consumer<Boolean> onCapture;
        private final BooleanCapture booleanCapture;

        // EFFECTS: Creates a new boolean capture state from the given
        //          capture function.
        public BooleanCaptureState(Consumer<Boolean> onCapture) {
            this.onCapture = onCapture;
            this.booleanCapture = new BooleanCapture(TRUE_STR,
                    FALSE_STR);
        }

        // MODIFIES: this
        // EFFECTS: Processes the next character and transitions state
        //          if necessary.
        @Override
        public State process(char input)
                throws ClothingAddressParseException {
            if (this.booleanCapture.foundBoolean(input)) {
                this.onCapture.accept(this.booleanCapture.getBoolCaptured());
                return new CapturingState();
            }
            return this;
        }
    }

}
