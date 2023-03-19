package ui.swing;

import java.awt.*;

// Wrapper class for builder pattern GridBagConstraints
public class GBC extends GridBagConstraints {

    // Enum wrapper for GridBagConstraints fill
    public enum Fill {
        None(GridBagConstraints.NONE),
        Both(GridBagConstraints.BOTH),
        Horizontal(GridBagConstraints.HORIZONTAL),
        Vertical(GridBagConstraints.VERTICAL);

        public final int wrappedValue;

        // EFFECTS: Constructs new enum with given wrapped value
        Fill(int wv) {
            this.wrappedValue = wv;
        }
    }

    // Enum wrapper for GridBagConstraints anchor
    public enum Anchor {
        Center(GridBagConstraints.CENTER),
        North(GridBagConstraints.NORTH),
        NorthEast(GridBagConstraints.NORTHEAST),
        East(GridBagConstraints.EAST),
        SouthEast(GridBagConstraints.SOUTHEAST),
        South(GridBagConstraints.SOUTH),
        SouthWest(GridBagConstraints.SOUTHWEST),
        West(GridBagConstraints.WEST),
        NorthWest(GridBagConstraints.NORTHWEST);

        public final int wrappedValue;

        // EFFECTS: Constructs new enum with given wrapped value
        Anchor(int wv) {
            this.wrappedValue = wv;
        }
    }

    // EFFECTS: Returns a new GBCBuilder with the given x and y
    public static GBC at(int x, int y) {
        return new GBC().gridx(x).gridy(y);
    }

    // MODIFIES: this
    // EFFECTS: Sets weight x/y and returns this
    public GBC weight(double x, double y) {
        return this.weightx(x).weighty(y);
    }

    // MODIFIES: this
    // EFFECTS: Sets fill and returns this
    public GBC fill(Fill f) {
        return this.fill(f.wrappedValue);
    }


    // MODIFIES: this
    // EFFECTS: Sets fill to v and returns this
    public GBC fill(int v) {
        this.fill = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets to fill horizontally
    public GBC fillHorizontal() {
        return this.fill(Fill.Horizontal);
    }

    // MODIFIES: this
    // EFFECTS: Sets anchor to given value
    public GBC anchor(Anchor a) {
        return this.anchor(a.wrappedValue);
    }

    // MODIFIES: this
    // EFFECTS: Sets anchor to v and returns this
    public GBC anchor(int v) {
        this.anchor = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets to fill vertically
    public GBC fillVertical() {
        return this.fill(Fill.Vertical);
    }

    // MODIFIES: this
    // EFFECTS: Sets fill to both
    public GBC fillBoth() {
        return this.fill(Fill.Both);
    }

    // MODIFIES: this
    // EFFECTS: Sets gridx to v and returns this
    public GBC gridx(int v) {
        this.gridx = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets gridy to v and returns this
    public GBC gridy(int v) {
        this.gridy = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets gridwidth to v and returns this
    public GBC gridwidth(int v) {
        this.gridwidth = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets gridheight to v and returns this
    public GBC gridheight(int v) {
        this.gridheight = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets weightx to v and returns this
    public GBC weightx(double v) {
        this.weightx = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets weighty to v and returns this
    public GBC weighty(double v) {
        this.weighty = v;
        return this;
    }


    // MODIFIES: this
    // EFFECTS: Sets insets to v and returns this
    public GBC insets(Insets v) {
        this.insets = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets ipadx to v and returns this
    public GBC ipadx(int v) {
        this.ipadx = v;
        return this;
    }

    // MODIFIES: this
    // EFFECTS: Sets ipady to v and returns this
    public GBC ipady(int v) {
        this.ipady = v;
        return this;
    }

}
