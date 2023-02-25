package model.search;

import model.Size;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

// A clothing address state machine for parsing a clothing address
// from a character-based input.
public class CAStateMachine
        extends StateMachine<CAStateMachine.State, ClothingAddressParseException> {


    public final String styleKey;
    public final String brandKey;
    public final String typeKey;
    public final String sizeKey;
    public final String isDirtyKey;
    public final String materialKey;
    public final String countKey;

    public final String trueSymbol;
    public final String falseSymbol;
    public final String equalitySymbol;
    public final String listSeparatorSymbol;
    public final String listEndSymbol;


    // EFFECTS: Creates a new clothing address state machine
    //          with default initial state and the given keys
    //          for parsing.
    public CAStateMachine(String styleKey,
                          String brandKey,
                          String typeKey,
                          String sizeKey,
                          String isDirtyKey,
                          String materialKey,
                          String trueSymbol,
                          String falseSymbol,
                          String equalitySymbol,
                          String listSeparatorSymbol,
                          String listEndSymbol,
                          String countKey) {
        super(null);

        this.styleKey = styleKey;
        this.brandKey = brandKey;
        this.typeKey = typeKey;
        this.sizeKey = sizeKey;
        this.isDirtyKey = isDirtyKey;
        this.materialKey = materialKey;
        this.trueSymbol = trueSymbol;
        this.falseSymbol = falseSymbol;
        this.equalitySymbol = equalitySymbol;
        this.listSeparatorSymbol = listSeparatorSymbol;
        this.listEndSymbol = listEndSymbol;
        this.countKey = countKey;

        this.setState(new CapturingState());
    }

    // EFFECTS: Retrieves the next state given the current internal
    //          state.
    @Override
    public State nextState(State in, char input)
            throws ClothingAddressParseException {
        return in.next(input);
    }

    // An abstract state representation for the clothing address
    // state machine with an internal clothing address to modify
    // as information entered is parsed.
    public abstract class State {

        private final ClothingAddress address;
        private final StringBuilder stateCaptured;

        // EFFECTS: Creates a new state from the current. If the previous
        //          base has no clothing address (null value), creates a new
        //          one for this state.
        public State() {
            if (getState() == null
                    || getState().getAddress() == null) {
                this.address = new ClothingAddress();
            } else {
                this.address = getState().getAddress();
            }
            this.stateCaptured = new StringBuilder();
        }

        // EFFECTS: Returns the clothing address constructed by
        //          this state.
        public ClothingAddress getAddress() {
            return this.address;
        }

        // EFFECTS: Returns the string processed by this specific state.
        public String getStateCaptured() {
            return this.stateCaptured.toString();
        }

        public final State next(char input)
                throws ClothingAddressParseException {
            this.stateCaptured.append(input);
            return this.process(input);
        }

        // MODIFIES: this
        // EFFECTS: Processes the given input, producing a new state.
        protected abstract State process(char input)
                throws ClothingAddressParseException;
    }

    // A state which parses outer filter items such as brand, size, etc
    public class CapturingState extends State {

        private final StringBuilder captured;
        private final KeyStringSearcher equalityStrSearcher;
        private final WhitespaceConsumer whitespaceConsumer;
        private Map<String, Supplier<State>> stateMap;

        // EFFECTS: Creates a new capturing state with the last state
        //          as the current one in this state machine.
        public CapturingState() {
            this.captured = new StringBuilder();
            this.whitespaceConsumer = new WhitespaceConsumer();
            this.equalityStrSearcher = new KeyStringSearcher(equalitySymbol);
        }

        // REQUIRES: this.initStateMap has not been called
        // MODIFIES: this
        // EFFECTS: Initializes this state map for the given keys
        private void initStateMap() {
            this.stateMap = new HashMap<String, Supplier<State>>() {
                {
                    put(brandKey, () -> new StringListCaptureState(
                            getAddress().getBrands()::addAll));
                    put(styleKey, () -> new StringListCaptureState(
                            getAddress().getStyles()::addAll));
                    put(typeKey, () -> new StringListCaptureState(
                            getAddress().getTypes()::addAll));
                    put(sizeKey, () -> new EnumListCaptureState<>(Size.class,
                            getAddress().getSizes()::addAll));
                    put(isDirtyKey, () -> new BooleanCaptureState(
                            getAddress()::setIsDirty));
                    put(materialKey, () -> new StringListCaptureState(
                            getAddress().getMaterials()::addAll));
                    put(countKey, () -> new IntegerCaptureState(
                            getAddress()::setMatchCount
                    ));
                }
            };
        }

        // EFFECTS: Returns whether this is in the process of matching
        //          or is dormant.
        public boolean isMatching() {
            return this.equalityStrSearcher.isMatching()
                    || captured.length() != 0;
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
            if (equalityTokenMatch.wasMatchRestarted()
                    || equalityTokenMatch.wasMatchBroken()) {
                this.captured.append(this.equalityStrSearcher.getPartialMatch());
            }
            if (equalityTokenMatch.wasMatch()) {
                return this.nextStateFromCaptured();
            } else if (equalityTokenMatch.wasNoMatch()) {
                captured.append(input);
            }
            return this;
        }

        // EFFECTS: Returns the next state dependent on the current
        //          string input. If this input is not found, returns
        //          null.
        private State nextStateFromCaptured() throws ClothingAddressParseException {
            String key = captured.toString().toLowerCase().trim();
            if (this.stateMap == null) {
                this.initStateMap();
            }
            if (stateMap.containsKey(key)) {
                return stateMap.get(key).get();
            } else {
                throw new NoSuchKeyException(this, key);
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
                    listSeparatorSymbol,
                    listEndSymbol
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
                    listSeparatorSymbol,
                    listEndSymbol);
        }

        // MODIFIES: this
        // EFFECTS: Processes the next character and returns the next state.
        @Override
        public State process(char input) throws ClothingAddressParseException {
            if (this.enumListCapture.isListFinished(input)) {
                List<T> captured = this.enumListCapture.getTokensCaptured();
                if (captured.contains(null)) {
                    throw new UnexpectedInputException(this, "Enum value did not match!");
                }
                this.onCapture.accept(captured);
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
            this.booleanCapture = new BooleanCapture(trueSymbol,
                    falseSymbol);
        }

        // MODIFIES: this
        // EFFECTS: Processes the next character and transitions state
        //          if necessary.
        @Override
        public State process(char input)
                throws ClothingAddressParseException {
            try {
                if (this.booleanCapture.foundBoolean(input)) {
                    this.onCapture.accept(this.booleanCapture.getBoolCaptured());
                    return new CapturingState();
                }
            } catch (UnexpectedBoolInputException e) {
                throw new UnexpectedInputException(this, e.getMessage());
            }
            return this;
        }
    }

    // Captures a single integer value, providing it to the consumer when it
    // is captured.
    public class IntegerCaptureState extends State {

        private final Consumer<Integer> onCapture;
        private final IntegerCapture integerCapture;

        // EFFECTS: Creates a new integer capture state with the given capture function
        public IntegerCaptureState(Consumer<Integer> onCapture) {
            this.onCapture = onCapture;
            this.integerCapture = new IntegerCapture(listEndSymbol);
        }

        // MODIFIES: this
        // EFFECTS: Searches for an integer in the input and returns the next state.
        @Override
        protected State process(char input) throws ClothingAddressParseException {
            try {
                if (integerCapture.foundInteger(input)) {
                    this.onCapture.accept(integerCapture.getIntegerCaptured());
                    return new CapturingState();
                }
            } catch (NumberFormatException e) {
                throw new UnexpectedInputException(this, "Expected an integer input.");
            }
            return this;
        }
    }

}
