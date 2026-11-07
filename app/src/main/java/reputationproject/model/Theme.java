package reputationproject.model;

public enum Theme {
    LIGHT("Light", "/view/light_theme.css"),
    DARK("Dark", "/view/dark_theme.css");

    private final String themeLabel;
    private final String stylesheet;

    private Theme(String label, String stylesheet) {
        this.themeLabel = label;
        this.stylesheet = stylesheet;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public String toString() {
        return themeLabel;
    }

    public static Theme fromName(String name) {
        for (Theme theme : values()) {
            if (theme.name().equals(name)) {
                return theme;
            }
        }
        return LIGHT;
    }

}
