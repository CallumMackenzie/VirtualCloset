package model.search;

// A checked exception for when a key is chosen that does not exist.
public class NoSuchKeyException extends ClothingAddressParseException {

    // EFFECTS: Creates a new exception with a message for the given key which
    //          was not found.
    public NoSuchKeyException(String key) {
        super("Key \"" + key + "\" does not exist and cannot be filtered for!");
    }
}
