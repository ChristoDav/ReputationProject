package reputationproject.model;

public enum Status {
    VERY_NEGATIVE("Very Negative"),
    NEGATIVE("Negative"),
    NEUTRAL("Neutral"),
    POSITIVE("Positive"),
    VERY_POSITIVE("Very Positive");

    public final String statusLabel;

    private Status(String label) {
        this.statusLabel = label;
    }

}
