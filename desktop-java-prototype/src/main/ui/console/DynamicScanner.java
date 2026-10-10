package ui.console;

import java.util.Scanner;
import java.util.Stack;

// A provider for a scanner object which can have value set
// across instances, as well as multiple internal scanners.
public class DynamicScanner {

    private final Stack<Scanner> scanners;

    // EFFECTS: Creates a new dynamic scanner with the given
    //          scanner input.
    public DynamicScanner(Scanner initial) {
        this.scanners = new Stack<>();
        this.scanners.push(initial);
    }

    // REQUIRES: this.hasNextLine is true
    // EFFECTS: Returns the next line from the highest priority
    //          internal scanner
    public String nextLine() {
        if (!this.hasNextLine()) {
            throw new RuntimeException("No next line in scanner!");
        }
        return this.scanners.peek().nextLine();
    }

    // MODIFIES: this
    // EFFECTS: Returns whether this scanner has a next line
    public boolean hasNextLine() {
        if (this.scanners.isEmpty()) {
            return false;
        }
        if (this.scanners.peek().hasNext()) {
            return true;
        }
        this.scanners.pop();
        return this.hasNextLine();
    }

    // REQUIRES: this.hasNextLine is true
    // EFFECTS: Returns the scanner object.
    public Scanner getScanner() {
        return this.scanners.peek();
    }

    // MODIFIES: this
    // EFFECTS: Sets the scanner for this object, making it
    //          the highest priority.
    public void addScanner(Scanner scanner) {
        this.scanners.push(scanner);
    }
}
