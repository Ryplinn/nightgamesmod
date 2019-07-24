package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Shield extends DurationStatus {
    private double strength;

    public Shield(CharacterType affected, double strength) {
        this(affected, strength, 7);
    }

    public Shield(CharacterType affected, double strength, int duration) {
        super("Shield", affected, duration);
        this.strength = strength;
        flag(Stsflag.shielded);
        flag(Stsflag.purgable);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now shielded.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public String describe(Combat c) {
        return "";
    }

    @Override
    public float fitnessModifier() {
        return (float) strength * 4;
    }

    @Override
    public int mod(Attribute a) {
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
        return (int) -Math.round(x * strength);
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
        return 2;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Shield(newAffected.getType(), strength, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("strength", strength);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Shield(null, obj.get("strength").getAsInt(), obj.get("duration").getAsInt());
    }
}
