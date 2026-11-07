package reputationproject.model.actor;

import java.util.ArrayList;
import reputationproject.model.Character;
import reputationproject.model.Relationship;
import reputationproject.model.Interaction;

public class Actor {
    private Character character;
    private ArrayList<Relationship> relationships;
    private ArrayList<Interaction> history;

    public Actor(Character character) {
        this.character = character;
        this.relationships = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public Actor(Character character, ArrayList<Relationship> relationships, ArrayList<Interaction> history) {
        this.character = character;
        this.relationships = relationships;
        if (history == null) {
            this.history = new ArrayList<>();
        } else {
            this.history = history;
        }
    }

    public Character getCharacter() {
        return character;
    }

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    public ArrayList<Interaction> getHistory() {
        return history;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void removeRelationship(Relationship relationship) {
        relationships.remove(relationship);
    }

}
