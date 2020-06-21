package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.damage.DamageType;

public class Horny extends DurationStatus {
    private float magnitude;
    protected String sourceSuffix;

    public static Horny getWithPsychologicalType(Character from, Character target, float magnitude, int duration, String source) {
        return new Horny(target.getType(), (float) DamageType.temptation.modifyDamage(from, target, magnitude), duration, source);
    }
    public static Horny getWithBiologicalType(Character from, Character target, float magnitude, int duration, String source) {
        return new Horny(target.getType(), (float) DamageType.biological.modifyDamage(from, target, magnitude), duration, source);
    }
    
    public Horny(CharacterType affected, float magnitude, int duration, String sourceSuffix) {
        super("Horny", affected, duration);
        this.sourceSuffix = sourceSuffix;
        this.magnitude = magnitude;
        flag(Stsflag.horny);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String toString() {
        return "Aroused from " + sourceSuffix + " (" + Formatter.formatDecimal(magnitude) + " x " + getDuration() + ")";
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your heart pounds in your chest as you try to suppress your arousal from contacting " + sourceSuffix
                            + ".";
        } else {
            return getAffected().getName() + " is flushed and "+getAffected().possessiveAdjective()
            +" nipples are noticeably hard from contacting " + sourceSuffix + ".";
        }
    }

    @Override
    public float fitnessModifier() {
        return -Math.min(.5f, magnitude * getDuration());
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        return 0;
    }

    @Override
    public void tick(Combat c) {
        getAffected().arouse(Math.round(magnitude), c, " (" + sourceSuffix + ")");
        getAffected().emote(Emotion.horny, 20);
    }

    @Override
    public String getVariant() {
        return sourceSuffix;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s%s aroused by %s.\n", getAffected().subjectAction("are", "is"),
                        replacement == null ? " now" : "",
                        sourceSuffix + " (" + Formatter.formatDecimal(magnitude) + " x " + getDuration() + ")");
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof Horny;
        Horny other = (Horny) s;
        assert other.sourceSuffix.equals(sourceSuffix);
        setDuration(other.getDuration());
        magnitude = other.magnitude;
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
    public boolean lingering() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Horny(newAffected.getType(), magnitude, getDuration(), sourceSuffix);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("source", sourceSuffix);
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Horny(null, obj.get("magnitude").getAsFloat(), obj.get("duration").getAsInt(),
                        obj.get("source").getAsString());
    }
    public float getMagnitude() {
        return magnitude;
    }
    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

}
