package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Alluring extends DurationStatus {
    public Alluring(Character affected, int duration) {
        super("Alluring", affected, duration);
        flag(Stsflag.alluring);
        flag(Stsflag.purgable);
    }

    public Alluring(Character affected) {
        this(affected, 3);
    }

    @Override
    public String initialMessage(Combat c, boolean replaced) {
        return String.format("%s now alluring.\n", affected.subjectAction("are", "is"));
    }

    @Override
    public String describe(Combat c) {
        if (!affected.human()) {
            return affected.name() + " looks impossibly beautiful to your eyes, you can't bear to hurt her.";
        }
        return "";
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public float fitnessModifier() {
        return 4.0f;
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
    public int weakened(int x) {
        return 0;
    }

    @Override
    public int tempted(int x) {
        return 0;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape() {
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
        return new Alluring(newAffected, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Alluring(null, obj.get("duration").getAsInt());
    }
}
