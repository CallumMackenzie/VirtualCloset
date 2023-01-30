package model;

// TODO
public enum Styles implements Style {
    Formal("Formal"),
    SemiFormal("Semi-formal"),
    Casual("Casual"),
    Outdoors("Outdoors"),
    Sport("Sport"),
    OutdoorWork("Outdoor work");

    private String name;

    Styles(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
