package model.search;

// A shorthand exception for clothing address parsing
public abstract class ClothingAddressParseException extends Exception {
    // EFFECTS: Creates a new clothing address parse exception with the given message
    public ClothingAddressParseException(String msg) {
        super(msg);
    }
}
