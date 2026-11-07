package reputationproject.model;

public enum Resolution {
    SMALL("Small", 800, 600),
    MEDIUM("Medium", 1000, 750),
    BIG("Big", 1280, 960);

    private final String resolutionLabel;
    private final int width;
    private final int height;

    private Resolution(String label, int width, int height) {
        this.resolutionLabel = label;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toString() {
        return resolutionLabel + " (" + width + " x " + height + ")";
    }

    public static Resolution fromName(String name) {
        for (Resolution resolution : values()) {
            if (resolution.name().equals(name)) {
                return resolution;
            }
        }
        return MEDIUM;
    }

}
