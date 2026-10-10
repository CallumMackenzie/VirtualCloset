package ui.swing.utils;

import java.awt.*;

// Wrapper class for builder pattern GridBagConstraints
public class GBC extends GridBagConstraints {

    // EFFECTS: Returns a new GBC with the given x and y
    public static GBC at(int x, int y) {
        return new GBC().gridx(x).gridy(y);
    }

    // EFFECTS: Returns a new GBC with the given x, y and with horizontal fill
    //          and a north anchor.
    public static GBC hfillNorth(int x, int y) {
        return GBC.at(x, y).hfill().anchor(Anchor.North);
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
    public GBC hfill() {
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
    // EFFECTS: Sets anchor to north
    public GBC north() {
        return this.anchor(Anchor.North);
    }

    // MODIFIES: this
    // EFFECTS: Sets anchor to center
    public GBC center() {
        return this.anchor(Anchor.Center);
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
    // EFFECTS: Sets insets to the given vals and returns this
    public GBC insets(int top, int left, int right, int bottom) {
        return this.insets(new Insets(top, left, bottom, right));
    }

    // MODIFIES: this
    // EFFECTS: Sets all insets to v and returns this
    public GBC insets(int v) {
        return this.insets(v, v, v, v);
    }

    // MODIFIES: this
    // EFFECTS: Sets top inset only to v and returns this
    public GBC bottomInset(int v) {
        this.insets.top = v;
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

    // MODIFIES: this
    // EFFECTS: Sets ipad x and y and returns this
    public GBC ipad(int x, int y) {
        return this.ipadx(x).ipady(y);
    }

    // MODIFIES: this
    // EFFECTS: Sets ipad x and y to v and returns this
    public GBC ipad(int v) {
        return this.ipad(v, v);
    }

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

}
