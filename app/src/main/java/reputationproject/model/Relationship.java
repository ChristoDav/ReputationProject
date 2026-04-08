package reputationproject.model;

import reputationproject.model.actor.Actor;

public class Relationship {
    private Actor target;
    private String type; // e.g., "friend", "acquaintance", "enemy"
    private int reputationValue; // -100 to 100
    private Status status;

    public Relationship(Actor target, int reputationValue, String type) {
        this.target = target;
        this.reputationValue = Math.max(-100, Math.min(100, reputationValue));
        this.type = type;
        this.status = determineStatus();
    }

    public Relationship(Actor target, String type) {
        this(target, 0, type);
    }

    public int getReputationValue() {
        return reputationValue;
    }

    public Actor getTargetActor() {
        return target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public int modifyReputationValue(int delta) {
        reputationValue = Math.max(-100, Math.min(100, reputationValue + delta));
        status = determineStatus();
        return reputationValue;
    }

    private Status determineStatus() {
        if (this.reputationValue < -80)
            return Status.VERY_NEGATIVE;
        else if (this.reputationValue < -20)
            return Status.NEGATIVE;
        else if (this.reputationValue < 20)
            return Status.NEUTRAL;
        else if (this.reputationValue < 80)
            return Status.POSITIVE;
        else
            return Status.VERY_POSITIVE;
    }

}
