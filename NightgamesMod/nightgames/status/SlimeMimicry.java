package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class SlimeMimicry extends DurationStatus {
    private final String mimickedName;

    public SlimeMimicry(String name, CharacterType affected, int duration) {
        super("Mimicry: " + Formatter.capitalizeFirstLetter(name), affected, duration);
        this.mimickedName = name;
        this.flag(Stsflag.mimicry);
        this.flag(Stsflag.form);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return Formatter.format("{self:SUBJECT} started mimicking a %s.", getAffected(), c.getOpponent(getAffected()), mimickedName);
    }

    @Override
    public String describe(Combat c) {
    	return Formatter.format("{self:SUBJECT-ACTION:are|is} mimicking a %s.", getAffected(), c.getOpponent(getAffected()), mimickedName);
    }

    @Override
    public int mod(Attribute a) {
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
        return new SlimeMimicry(getMimickedName(), newAffected.getType(), getDuration());
    }

    @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("mimickedName", getMimickedName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new SlimeMimicry(obj.get("mimickedName").getAsString(),
                        null,
                        obj.get("duration").getAsInt());
    }

    public String getMimickedName() {
        return mimickedName;
    }

}
