package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Glamour extends DurationStatus {
    public Glamour(CharacterType affected, int duration) {
        super("Glamour", affected, duration);
        flag(Stsflag.alluring);
        flag(Stsflag.glamour);
        flag(Stsflag.purgable);
    }

    public Glamour(CharacterType affected) {
        this(affected, 3);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s surrounded %s with a arcane glamour.\n", getAffected().subjectAction("have", "has"), getAffected().reflectivePronoun());
    }

    @Override
    public String describe(Combat c) {
        if (!getAffected().human()) {
            return String.format("%s inhumanly beautiful.",
                            c.getOpponent(getAffected()).subjectAction("look", "looks"));
        }
        return "";
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public float fitnessModifier() {
        return 2.0f;
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
        return 0;
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
    public Status instance(Character newAffected, Character newOther) {
        return new Glamour(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Glamour(null, obj.get("duration").getAsInt());
    }
}
