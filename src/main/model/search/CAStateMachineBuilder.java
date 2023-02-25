package model.search;

// A builder for the CAStateMachine class
public class CAStateMachineBuilder {

    public static final String STYLE_CAPTURE_STR = "style";
    public static final String BRAND_CAPTURE_STR = "brand";
    public static final String TYPE_CAPTURE_STR = "type";
    public static final String SIZE_CAPTURE_STR = "size";
    public static final String IS_DIRTY_CAPTURE_STR = "dirty";
    public static final String MATERIAL_CAPTURE_STR = "material";
    public static final String TRUE_STR = "yes";
    public static final String FALSE_STR = "no";
    public static final String EQUALITY_STR = "=";
    public static final String LIST_SEPARATOR_STR = ",";
    public static final String LIST_END_STR = ";";
    public static final String COUNT_STR = "count";

    private String styleKey;
    private String brandKey;
    private String typeKey;
    private String sizeKey;
    private String isDirtyKey;
    private String materialKey;
    private String trueSymbol;
    private String falseSymbol;
    private String equalitySymbol;
    private String listSeparatorSymbol;
    private String listEndSymbol;
    private String countKey;

    // EFFECTS: Creates a default CAStateMachine
    public static CAStateMachine buildDefault() {
        return new CAStateMachineBuilder().build();
    }

    // EFFECTS: Creates a new CAStateMachineBuilder with default
    //          parameters as seen in the constants of this class.
    public CAStateMachineBuilder() {
        this.styleKey = STYLE_CAPTURE_STR;
        this.brandKey = BRAND_CAPTURE_STR;
        this.typeKey = TYPE_CAPTURE_STR;
        this.sizeKey = SIZE_CAPTURE_STR;
        this.isDirtyKey = IS_DIRTY_CAPTURE_STR;
        this.materialKey = MATERIAL_CAPTURE_STR;
        this.trueSymbol = TRUE_STR;
        this.falseSymbol = FALSE_STR;
        this.equalitySymbol = EQUALITY_STR;
        this.listSeparatorSymbol = LIST_SEPARATOR_STR;
        this.listEndSymbol = LIST_END_STR;
        this.countKey = COUNT_STR;
    }

    // EFFECTS: Returns a new CAStateMachine with the fields of this
    //          class as parameters.
    public CAStateMachine build() {
        return new CAStateMachine(styleKey,
                brandKey,
                typeKey,
                sizeKey,
                isDirtyKey,
                materialKey,
                trueSymbol,
                falseSymbol,
                equalitySymbol,
                listSeparatorSymbol,
                listEndSymbol,
                countKey);
    }

    // MODIFIES: this
    // EFFECTS: Sets the style key to the given, and returns this.
    public CAStateMachineBuilder styleKey(String v) {
        this.styleKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the brandKey to the given, and returns this.
    public CAStateMachineBuilder brandKey(String v) {
        this.brandKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the typeKey to the given, and returns this.
    public CAStateMachineBuilder typeKey(String v) {
        this.typeKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the sizeKey to the given, and returns this.
    public CAStateMachineBuilder sizeKey(String v) {
        this.sizeKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the isDirtyKey to the given, and returns this.
    public CAStateMachineBuilder isDirtyKey(String v) {
        this.isDirtyKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the materialKey to the given, and returns this.
    public CAStateMachineBuilder materialKey(String v) {
        this.materialKey = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the trueSymbol to the given, and returns this.
    public CAStateMachineBuilder trueSymbol(String v) {
        this.trueSymbol = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the falseSymbol to the given, and returns this.
    public CAStateMachineBuilder falseSymbol(String v) {
        this.falseSymbol = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the equalitySymbol to the given, and returns this.
    public CAStateMachineBuilder equalitySymbol(String v) {
        this.equalitySymbol = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the listSeparatorSymbol to the given, and returns this.
    public CAStateMachineBuilder listSeparatorSymbol(String v) {
        this.listSeparatorSymbol = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the listEndSymbol to the given, and returns this.
    public CAStateMachineBuilder listEndSymbol(String v) {
        this.listEndSymbol = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets the countKey to the given, and returns this
    public CAStateMachineBuilder countKey(String v) {
        this.countKey = v;
        return this;
    }

}
