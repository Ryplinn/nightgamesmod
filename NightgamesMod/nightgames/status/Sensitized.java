package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.json.JsonUtils;

public class Sensitized extends DurationStatus {
    private BodyPart part;
    double magnitude;
    private double maximum;

    public Sensitized(CharacterType affected, BodyPart part, double magnitude, double maximum, int duration) {
        super("Sensitized (" + part.getType() + ")", affected, duration);
        this.part = part;
        this.magnitude = magnitude;
        this.maximum = maximum;
        flag(Stsflag.sensitized);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public float fitnessModifier() {
        return (float) (-magnitude * 5);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null)
            return "";
        return Formatter.format(String.format("{self:NAME-POSSESSIVE} groans as {self:possessive} %s grows hot.",
                        part.describe(getAffected())), getAffected(), c.getOpponent(getAffected()));
    }

    @Override
    public String describe(Combat c) {
        return "";
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
        if (targetPart.isType(part.getType())) {
            return x * magnitude;
        }
        return 0;
    }

    public boolean overrides(Status s) {
        return s.name.equals(this.name);
    }

    public void replace(Status newStatus) {
        if (newStatus instanceof Sensitized) {
            this.magnitude = Math.min(maximum, magnitude + ((Sensitized) newStatus).magnitude);
            setDuration(Math.max(((Sensitized) newStatus).getDuration(), getDuration()));
        }
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
    public boolean lingering() {
        return true;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Sensitized(newAffected.getType(), part, magnitude, maximum, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("maximum", maximum);
        obj.addProperty("duration", getDuration());
        obj.add("part", JsonUtils.getGson().toJsonTree(part));
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Sensitized(NPC.noneCharacter().getType(), JsonUtils.getGson().fromJson(obj.get("part"), BodyPart.class), obj.get("magnitude").getAsFloat(),
                        obj.get("maximum").getAsFloat(), obj.get("duration").getAsInt());
    }

}
