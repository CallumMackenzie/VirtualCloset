package model.search;

// A class representing unexpected char input
public class UnexpectedInputException extends ClothingAddressParseException {
    // EFFECTS: Creates a new unexpected input exception with the given
    //          message.
    public UnexpectedInputException(CAStateMachine.State errorState, String msg) {
        super(errorState, msg);
    }
}
