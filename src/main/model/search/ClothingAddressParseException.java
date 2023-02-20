package model.search;

// A shorthand exception for clothing address parsing containing the parsing
// state where the error occurred.
public abstract class ClothingAddressParseException extends Exception {

    private final CAStateMachine.State errorState;

    // EFFECTS: Creates a new clothing address parse exception with the given message
    public ClothingAddressParseException(CAStateMachine.State errorState,
                                         String msg) {
        super(msg);
        this.errorState = errorState;
    }

    // EFFECTS: Returns the state where the exception originated.
    public CAStateMachine.State getErrorState() {
        return this.errorState;
    }
}
