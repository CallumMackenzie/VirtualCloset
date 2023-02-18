package model.search;

import java.util.List;

// TODO
public interface ListCapture<T> {
    // TODO
    boolean isListFinished(char input);

    // TODO
    List<T> getTokensCaptured();

    // TODO
    String getListSeparatorString();

    // TODO
    String getListTerminatorString();
}
