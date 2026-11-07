package reputationproject.model;

public class World {
    private int id;
    private String name;

    public World(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

}
