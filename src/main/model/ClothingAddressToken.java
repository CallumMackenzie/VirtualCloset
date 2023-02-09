package model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ClothingAddressToken {
    EQUALITY_TOKEN {
        @Override
        protected void parse(State state) {
            // Preconditions
            ClothingAddressToken.assertHasInput(state);
            if (state.history.isEmpty()) {
                throw new MalformedClothingAddressException(
                        "No specifier before \""
                                + EQUALITY_TOKEN_STR + "\".");
            }

            // Effects
            state.history.push(EQUALITY_TOKEN);
            switch (state.history.peek()) {
                case BRAND_TOKEN:
                case SIZE_TOKEN:
                case TYPE_TOKEN:
                case STYLE_TOKEN:
                    MATCH_VALUE.parse(state);
                    break;
                default:
                    throw new MalformedClothingAddressException(
                            "Unexpected token before equality \""
                                    + EQUALITY_TOKEN_STR + "\": "
                                    + state.history.peek());
            }
        }
    },
    BRAND_TOKEN {
        @Override
        protected void parse(State state) {
            ClothingAddressToken.assertHasInput(state);
            ClothingAddressToken.parseEqualityType(
                    BRAND_TOKEN_STR, BRAND_TOKEN, state
            );
        }
    },
    SIZE_TOKEN {
        @Override
        protected void parse(State state) {
            ClothingAddressToken.assertHasInput(state);
            ClothingAddressToken.parseEqualityType(
                    SIZE_TOKEN_STR, SIZE_TOKEN, state
            );
        }
    },
    TYPE_TOKEN {
        @Override
        protected void parse(State state) {
            ClothingAddressToken.assertHasInput(state);
            ClothingAddressToken.parseEqualityType(
                    TYPE_TOKEN_STR, TYPE_TOKEN, state
            );
        }
    },
    STYLE_TOKEN {
        @Override
        protected void parse(State state) {
            ClothingAddressToken.assertHasInput(state);
            ClothingAddressToken.parseEqualityType(
                    STYLE_TOKEN_STR, STYLE_TOKEN, state
            );
        }
    },
    LIST_SEPARATOR_TOKEN {
        @Override
        protected void parse(State state) {
            ClothingAddressToken last = state.history.pop();
            switch (last) {
                case MATCH_VALUE:
                    break;
                case LIST_SEPARATOR_TOKEN:
                    throw new MalformedClothingAddressException(
                            "Two list separators in a row!");
                case EQUALITY_TOKEN:
                    throw new MalformedClothingAddressException(
                            "Equality token preceding list separator!");
                default:
                    throw new MalformedClothingAddressException(
                            "Unexpected token preceding list separator: "
                                    + last);
            }
        }
    },
    MATCH_VALUE {
        @Override
        protected void parse(State state) {
            ClothingAddressToken last = state.history.pop();
            if (last != EQUALITY_TOKEN
                    && last != LIST_SEPARATOR_TOKEN) {
                throw new MalformedClothingAddressException(
                        "Value not preceded by list or equality token.");
            }
        }
    };

    // TODO
    private static class State {
        final Scanner input;
        final ClothingAddress address;
        final Stack<ClothingAddressToken> history;

        public State(Scanner input,
                     ClothingAddress address) {
            this.input = input;
            this.address = address;
            this.history = new Stack<>();
        }
    }

    public static final String EQUALITY_TOKEN_STR = ":";
    public static final String BRAND_TOKEN_STR = "b";
    public static final String SIZE_TOKEN_STR = "sz";
    public static final String TYPE_TOKEN_STR = "t";
    public static final String STYLE_TOKEN_STR = "st";
    public static final String LIST_SEPARATOR_STR = ",";

    private static void assertHasInput(State s) {
        if (!s.input.hasNext()) {
            throw new MalformedClothingAddressException("Unexpected end of input.");
        }
    }

    // Modifies: state
    // TODO
    protected abstract void parse(State state);

    // TODO
    public static ClothingAddress parse(Scanner input) {
        State inputState = new State(input, new ClothingAddress());
        assertHasInput(inputState);

        while (input.hasNext()) {
            System.out.println(input.next());
        }

        String first = input.next();
        ClothingAddressToken firstToken =
                Map.of(BRAND_TOKEN_STR, BRAND_TOKEN,
                                SIZE_TOKEN_STR, SIZE_TOKEN,
                                TYPE_TOKEN_STR, TYPE_TOKEN,
                                STYLE_TOKEN_STR, STYLE_TOKEN)
                        .get(first);
        firstToken.parse(inputState);
        return inputState.address;
    }

    // TODO
    private static void parseEqualityType(
            String strToken,
            ClothingAddressToken token,
            State state
    ) {
        state.history.push(token);
        switch (state.input.next()) {
            case EQUALITY_TOKEN_STR:
                EQUALITY_TOKEN.parse(state);
                break;
            case LIST_SEPARATOR_STR:
                LIST_SEPARATOR_TOKEN.parse(state);
                break;
            default:
                throw new MalformedClothingAddressException(
                        "Unexpected token \"" + strToken + "\".");
        }
    }
}