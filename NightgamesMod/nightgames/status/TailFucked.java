package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import static nightgames.requirements.RequirementShortcuts.eitherinserted;

public class TailFucked extends Status implements InsertedStatus {
    private String target;
    private CharacterType other;

    public TailFucked(CharacterType affected, CharacterType other, String hole) {
        super(hole.equals("ass") ? "Tail Pegged" : "Tail Fucked", affected);
        target = hole;
        this.other = other;
        requirements.add(eitherinserted());
        flag(Stsflag.bound);
        flag(Stsflag.debuff);
        flag(Stsflag.tailfucked);
    }

    private Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        BodyPart hole = getAffected().body.getRandom(target);
        BodyPart tail = getOther().body.getRandom("tail");
        if (hole == null || tail == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s now fucking %s %s with %s %s\n",
                        getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(), hole.describe(getAffected()),
                        getOther().possessiveAdjective(), tail.describe(getOther())));
    }

    @Override
    public String describe(Combat c) {
        BodyPart hole = getAffected().body.getRandom(target);
        BodyPart tail = getOther().body.getRandom("tail");
        if (hole == null || tail == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s fucking %s %s with %s %s\n",
                            getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(),
                            hole.describe(getAffected()), getOther().possessiveAdjective(), tail.describe(getOther())));
    }

    @Override
    public float fitnessModifier() {
        return -3;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void tick(Combat c) {
        BodyPart hole = getAffected().body.getRandom(target);
        BodyPart tail = getOther().body.getRandom("tail");
        if (hole == null || tail == null || c == null) {
            getAffected().removelist.add(this);
            return;
        }
        c.write(getOther(), Formatter.capitalizeFirstLetter(
                        Formatter.format("{other:name-possessive} {other:body-part:tail} relentlessly fucks {self:name-do} in {self:possessive} {self:body-part:"
                                        + target + "}.", getAffected(), getOther())));
        getAffected().body.pleasure(getOther(), tail, hole, 10, c);
        getOther().body.pleasure(getAffected(), hole, tail, 2, c);
        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
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
        return -5;
    }

    @Override
    public int escape(Character from) {
        return -5;
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
        return -10;
    }

    @Override
    public String toString() {
        return "Tail fucked";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new TailFucked(newAffected.getType(), newOther.getType(), target);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("target", target);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new TailFucked(null, null, obj.get("target").getAsString());
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }

    @Override
    public BodyPart getHolePart() {
        return getAffected().body.getRandom(target);
    }

    @Override
    public Character getReceiver() {
        return getAffected();
    }

    @Override
    public BodyPart getStickPart() {
        return getOther().body.getRandom("tail");
    }

    @Override
    public Character getPitcher() {
        return getOther();
    }
}
