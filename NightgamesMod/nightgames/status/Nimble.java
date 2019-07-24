package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Nimble extends DurationStatus {
    public Nimble(CharacterType affected, int duration) {
        super("Nimble", affected, duration);
        flag(Stsflag.nimble);
        flag(Stsflag.purgable);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You're as quick and nimble as a cat.";
        } else {
            return getAffected().getName() + " darts around gracefully.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now more nimble.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return getAffected().get(Attribute.animism) / 10.0f;
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.speed) {
            return 2 + getAffected().getArousal().getReal() / 100;
        }
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.confident, 5);
        return 0;
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
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return getAffected().get(Attribute.animism) * getAffected().getArousal().percent() / 100;
    }

    @Override
    public int escape(Character from) {
        return getAffected().get(Attribute.animism) * getAffected().getArousal().percent() / 100;
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
        return (getAffected().get(Attribute.animism) / 2) * getAffected().getArousal().percent() / 100;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Nimble(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Nimble(null, obj.get("duration").getAsInt());
    }
}
