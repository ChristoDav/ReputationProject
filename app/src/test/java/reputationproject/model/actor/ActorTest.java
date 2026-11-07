package reputationproject.model.actor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import reputationproject.model.Character;
import reputationproject.model.Interaction;

class ActorTest {

    @Test
    void actorHoldsTheInWorldCharacter() {
        Character character = new Character();

        assertSame(character, new Actor(character).getCharacter());
    }

    @Test
    void playerActorHoldsTheInWorldCharacter() {
        Character character = new Character();

        assertSame(character, new PlayerActor("Chris", character).getCharacter());
    }

    @Test
    void nullHistoryBecomesAnEmptyHistory() {
        Actor actor = new Actor(new Character(), new ArrayList<>(), null);

        assertTrue(actor.getHistory().isEmpty());
    }

    @Test
    void providedHistoryIsKept() {
        ArrayList<Interaction> history = new ArrayList<>();
        history.add(new Interaction("Saved their life", 50));

        Actor actor = new Actor(new Character(), new ArrayList<>(), history);

        assertEquals(1, actor.getHistory().size());
    }
}
