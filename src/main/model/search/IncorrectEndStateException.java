package model.search;

// An exception for ending the parsing of a clothing address on a non-final state
public class IncorrectEndStateException extends ClothingAddressParseException {

    // EFFECTS: Creates a new incorrect end state exception with the given message.
    public IncorrectEndStateException(CAStateMachine.State errorState, String msg) {
        super(errorState, msg);
    }
}
