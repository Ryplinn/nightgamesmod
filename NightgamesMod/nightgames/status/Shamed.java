package nightgames.status;

import java.util.Optional;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Shamed extends DurationStatus {

    private int magnitude;

    public Shamed(CharacterType affected) {
        super("Shamed", affected, 4);
        flag(Stsflag.shamed);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        flag(Stsflag.mindgames);
        magnitude = 1;
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You're a little distracted by self-consciousness, and it's throwing you off your game.";
        } else {
            return getAffected().getName() + " is red faced from embarrassment as much as arousal.";
        }
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        return String.format("%s now shamed.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -1.0f * magnitude;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        getAffected().addlist.add(new Cynical(affected));
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.seduction || a == Attribute.cunning) {
            return Math.min(-2 * magnitude, -getAffected().getPure(a) * magnitude / 5);
        } else if (a == Attribute.submission && getAffected().getPure(Attribute.submission) > 0) {
            return magnitude;
        } else {
            return 0;
        }
    }

    @Override
    public void tick(Combat c) {
        getAffected().emote(Emotion.nervous, 20);
        if (getAffected().getPure(Attribute.submission) > 0) {
            getAffected().buildMojo(c, 3 * magnitude, " (Shamed)");
        } else {
            getAffected().loseMojo(c, 5 * magnitude, " (Shamed)");
        }
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return magnitude;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 2 * magnitude;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape() {
        return -2 * magnitude;
    }

    @Override
    public int gainmojo(int x) {
        return -x * magnitude / 2;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Shamed(newAffected.getType());
    }

    @Override
    public void replace(Status newStatus) {
        assert newStatus instanceof Shamed;
        Shamed other = (Shamed) newStatus;
        setDuration(Math.max(other.getDuration(), getDuration()));
        magnitude += other.magnitude;
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Shamed(null);
    }
}
