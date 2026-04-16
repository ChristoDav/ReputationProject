package reputationproject.model;

public class Interaction {
    private String description;
    private int valueChange;

    public Interaction(String description, int valueChange) {
        this.description = description;
        this.valueChange = valueChange;
    }

    public String getDescription() {
        return description;
    }

    public int getValueChange() {
        return valueChange;
    }

}
