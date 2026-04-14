package reputationproject.respository;

import java.util.ArrayList;
import reputationproject.model.actor.Actor;

public class AppState {
    private static AppState instance;

    private ArrayList<Actor> actors;

    private AppState() {
        actors = new ArrayList<>();
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }
}
