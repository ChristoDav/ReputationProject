package reputationproject.model;

public enum Page {
    HOME("Home"),
    ACTORS("Actors"),
    SETTINGS("Settings");

    private final String pageLabel;

    private Page(String label) {
        this.pageLabel = label;
    }

    public String toString() {
        return pageLabel;
    }

}
