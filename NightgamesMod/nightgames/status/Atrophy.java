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

public class Atrophy extends DurationStatus {
    private float magnitude;
    protected String source;

    public static Status getWithPsychologicalType(Character from, Character target, float magnitude, int duration, String source) {
        return new Atrophy(target.getType(), (float) DamageType.temptation.modifyDamage(from, target, magnitude), duration, source);
    }
    public static Atrophy getWithBiologicalType(Character from, Character target, float magnitude, int duration, String source) {
        return new Atrophy(target.getType(), (float) DamageType.biological.modifyDamage(from, target, magnitude), duration, source);
    }

    public Atrophy(CharacterType affected, float magnitude, int duration, String source) {
        super("Horny", affected, duration);
        this.source = source;
        this.magnitude = magnitude;
        flag(Stsflag.atrophy);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String toString() {
        return "Weakened from " + source + " (" + Formatter.formatDecimal(magnitude) + " x " + getDuration() + ")";
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You feel sluggish from " + source + ".";
        } else {
            return getAffected().getName() + " is visibly weakened from contacting " + source + ".";
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
        getAffected().weaken(c, Math.round(magnitude));
        getAffected().emote(Emotion.nervous, 5);
    }

    @Override
    public String getVariant() {
        return source;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s %sweakening from %s.\n", getAffected().subjectAction("are", "is"), replacement != null ? "" : "now ",
                        source + " (" + Formatter.formatDecimal(magnitude) + " x " + getDuration() + ")");
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof Atrophy;
        Atrophy other = (Atrophy) s;
        assert other.source.equals(source);
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
        return new Atrophy(newAffected.getType(), magnitude, getDuration(), source);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("source", source);
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    public Status loadFromJson(JsonObject obj) {
        return new Atrophy(null, obj.get("magnitude").getAsFloat(), obj.get("duration").getAsInt(),
                        obj.get("source").getAsString());
    }
    public float getMagnitude() {
        return magnitude;
    }
    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }
}
