package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;

public class Lethargic extends DurationStatus {
    double magnitude;

    public Lethargic(CharacterType affected, int duration, double magnitude) {
        super("Lethargic", affected, duration);
        this.magnitude = magnitude;
        flag(Stsflag.lethargic);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public boolean lingering() {
        return true;
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your mojo gain is stopped.";
        } else if (getAffected().has(Trait.lethargic)) {
            if (getAffected().getMojo().get() < 40) {
                return getAffected().getName() + " looks lethargic.";
            } else {
                return getAffected().getName() + " looks energized";
            }
        } else {
            return getAffected().getName() + " looks lethargic.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s lethargic.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -3f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
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
        return 10;
    }

    @Override
    public int escape(Character from) {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return (int) Math.round(-x * magnitude);
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
    public Status instance(Character newAffected, Character opponent) {
        return new Lethargic(newAffected.getType(), getDuration(), magnitude);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        obj.addProperty("magnitude", magnitude);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Lethargic(null, obj.get("duration").getAsInt(), obj.get("magnitude").getAsFloat());
    }
}
