package model.search;

// An unexpected boolean input with a message
public class UnexpectedBoolInputException extends Exception {
    // EFFECTS: Creates a new exception with the given message
    public UnexpectedBoolInputException(String s) {
        super(s);
    }
}
