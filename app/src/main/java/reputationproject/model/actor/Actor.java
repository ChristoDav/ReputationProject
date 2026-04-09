package reputationproject.model.actor;

import java.util.ArrayList;
import reputationproject.model.Relationship;

public class Actor {
    private Character character;
    private ArrayList<Relationship> relationships;

    public Actor(Character character) {
        this.character = character;
        this.relationships = new ArrayList<>();
    }

    public Actor(Character character, ArrayList<Relationship> relationships) {
        this.character = character;
        this.relationships = relationships;
    }

    public Character getCharacter() {
        return character;
    }

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public void removeRelationship(Relationship relationship) {
        relationships.remove(relationship);
    }

}
