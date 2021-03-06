package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Buzzed extends DurationStatus {

    private int magnitude;

    public Buzzed(CharacterType affected) {
        super("Buzzed", affected, 20);
        flag(Stsflag.purgable);
        magnitude = 1;
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You feel a pleasant buzz, which makes you a bit sluggish, but also takes the edge of your sense of touch.";
        } else {
            return getAffected().getName() + " looks mildly buzzed, probably trying to dull "+getAffected().possessiveAdjective()+" senses.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now buzzed.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return 0.0f;
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.perception) {
            return -3 * magnitude;
        } else if (a == Attribute.power) {
            return -magnitude;
        } else if (a == Attribute.cunning) {
            return -2 * magnitude;
        }
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.confident, 15);
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return -x / (10.0 / magnitude);
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return -5 * magnitude;
    }

    @Override
    public int escape(Character from) {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
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
    public boolean lingering() {
        return true;
    }

    @Override
    public void replace(Status newStatus) {
        assert newStatus instanceof Buzzed;
        Buzzed other = (Buzzed) newStatus;
        setDuration(Math.max(other.getDuration(), getDuration()));
        magnitude += other.magnitude;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Buzzed(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Buzzed(null);
    }
}
