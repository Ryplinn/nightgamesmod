package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class CockChoked extends DurationStatus {
    CharacterType other;

    public CockChoked(CharacterType affected, CharacterType other, int duration) {
        super("Cock Choked", affected, duration);
        this.other = other;
        flag(Stsflag.orgasmseal);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now preventing %s from cumming\n", getOther().subjectAction("are", "is"),
                        getAffected().subject());
    }

    @Override
    public String describe(Combat c) {
        return String.format("%s preventing %s from cumming\n", getOther().subjectAction("are", "is"), getAffected().subject());
    }

    @Override
    public float fitnessModifier() {
        if (getAffected().getArousal().percent() > 80) {
            return -10;
        }
        return 0;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        getAffected().addlist.add(new Wary(affected, 2));
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        if (getAffected().getArousal().percent() > 80) {
            getAffected().emote(Emotion.desperate, 10);
            getAffected().emote(Emotion.horny, 10);
        }
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
    public String toString() {
        return "Cock Choked";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new CockChoked(newAffected.getType(), opponent.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new CockChoked(null, null, obj.get("duration").getAsInt());
    }
}
