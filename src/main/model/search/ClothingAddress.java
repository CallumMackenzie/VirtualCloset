package model.search;

import model.Size;

import java.util.ArrayList;
import java.util.List;

// Search parameters for an article of clothing.
public final class ClothingAddress {

    private final List<String> brands;
    private final List<Size> sizes;
    private final List<String> styles;
    private final List<String> types;
    private final List<String> materials;
    private Boolean isDirty;

    // EFFECTS: Creates a new clothing address with all empty lists
    //          and default values.
    public ClothingAddress() {
        this.brands = new ArrayList<>();
        this.sizes = new ArrayList<>();
        this.styles = new ArrayList<>();
        this.types = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.isDirty = null;
    }

    // EFFECTS: Parses the given string expression into a clothing address.
    public static ClothingAddress of(String expr) throws ClothingAddressParseException {
        CAStateMachine parser = new CAStateMachine();
        CAStateMachine.State out = parser.processInput(expr.toCharArray());
        final String listEndDelimMissingMsg = "(is there a \"" + CAStateMachine.LIST_END_STR + "\" present?)";
        if (out instanceof CAStateMachine.StringListCaptureState) {
            throw new IncorrectEndStateException(out,
                    "Unfinished string list in expression! " + listEndDelimMissingMsg);
        } else if (out instanceof CAStateMachine.EnumListCaptureState) {
            throw new IncorrectEndStateException(out,
                    "Unfinished enum list in expression! " + listEndDelimMissingMsg);
        } else if (out instanceof CAStateMachine.BooleanCaptureState) {
            throw new IncorrectEndStateException(out, "Unfinished boolean value in expression!");
        } else if (out instanceof CAStateMachine.CapturingState) {
            CAStateMachine.CapturingState o = (CAStateMachine.CapturingState) out;
            if (o.isMatching()) {
                throw new IncorrectEndStateException(out, "Incomplete key capture expression!");
            }
        }
        return out.getAddress();
    }

    // EFFECTS: Returns whether it is searching for clean or dirty
    //          clothing. True or false correspond to their values,
    //          but null means there is no search preference.
    public Boolean getIsDirty() {
        return this.isDirty;
    }

    // MODIFIES: this
    // EFFECTS: Sets whether to search for dirty clothing, clean
    //          or no preference.
    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    // Effects: Returns the brands this address matches
    public List<String> getBrands() {
        return brands;
    }

    // Effects: Returns the sizes this address matches
    public List<Size> getSizes() {
        return sizes;
    }

    // Effects: Returns the styles this address matches
    public List<String> getStyles() {
        return styles;
    }

    // Effects: Returns the types this address matches
    public List<String> getTypes() {
        return types;
    }

    // EFFECTS: Returns the materials this address matches
    public List<String> getMaterials() {
        return this.materials;
    }

}
