package model.search;

// An abstract state machine with a current state of type S,
// initial state, and next state function with some utilities
// for processing more lengthy inputs.
public abstract class StateMachine<S> {

    private S state;

    // EFFECTS: Creates a new state machine with the given initial state
    public StateMachine(S initialState) {
        this.state = initialState;
    }

    // MODIFIES: this
    // EFFECTS: Steps the state machine once, assigning the result of
    //          the next state function to the internal current state.
    public final void step(char input) {
        this.state = this.nextState(this.state, input);
    }

    // MODIFIES: this
    // REQUIRES: input != null
    // EFFECTS: Steps the state for each input in the given char array.
    public final S processInput(char[] input) {
        for (char c : input) {
            this.step(c);
        }
        return this.state;
    }

    // EFFECTS: Returns the current state for this machine.
    public S getState() {
        return this.state;
    }

    // EFFECTS: The next state function to be implemented by inheriting
    //          classes.
    public abstract S nextState(S in, char input);

}
