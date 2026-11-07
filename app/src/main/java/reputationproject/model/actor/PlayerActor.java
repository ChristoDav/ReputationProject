package reputationproject.model.actor;

import java.util.ArrayList;

import reputationproject.model.Character;
import reputationproject.model.Interaction;
import reputationproject.model.Relationship;

public class PlayerActor extends Actor {
    private String playerName;

    public PlayerActor(String playerName, Character character) {
        super(character);
        this.playerName = playerName;
    }

    public PlayerActor(String playerName, Character character, ArrayList<Relationship> relationships, ArrayList<Interaction> history) {
        super(character, relationships, history);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
