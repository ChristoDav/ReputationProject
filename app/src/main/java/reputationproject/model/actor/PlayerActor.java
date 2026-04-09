package reputationproject.model.actor;

import java.util.ArrayList;

import reputationproject.model.Relationship;

public class PlayerActor extends Actor {
    private String playerName;

    public PlayerActor(String playerName, Character character) {
        super(character);
        this.playerName = playerName;
    }

    public PlayerActor(String playerName, Character character, ArrayList<Relationship> relationships) {
        super(character, relationships);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
