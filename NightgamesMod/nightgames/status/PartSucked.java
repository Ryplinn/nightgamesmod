package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.json.JsonUtils;

public class PartSucked extends Status implements InsertedStatus {
    private String target;
    private CharacterType other;
    private BodyPart penetrated;

    public PartSucked(CharacterType affected, CharacterType other, BodyPart penetrated, String targetType) {
        super(penetrated + " Sucked", affected);
        target = targetType;
        this.penetrated = penetrated;
        this.other = other;
        requirements.add((c, self, opponent) -> {
            if (c.getStance().distance() > 1) {
                return false;
            }
            return false;
        });
        flag(Stsflag.debuff);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        BodyPart stick = getAffected().body.getRandom(target);
        if (stick == null || penetrated == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s now fucking %s %s with %s %s\n",
                        getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(), stick.describe(getAffected()),
                        getOther().possessiveAdjective(), penetrated.describe(getOther())));
    }

    private Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String describe(Combat c) {
        BodyPart stick = getAffected().body.getRandom(target);
        if (stick == null || penetrated == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s fucking %s %s with %s %s\n",
                            getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(),
                            stick.describe(getAffected()), getOther().possessiveAdjective(), penetrated.describe(getOther())));
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
        BodyPart stick = getAffected().body.getRandom(target);
        if (stick == null || penetrated == null || c == null) {
            getAffected().removelist.add(this);
            return;
        }
        c.write(getOther(), Formatter.capitalizeFirstLetter(
                        Formatter.format("{other:name-possessive} %s mindlessly milks {self:name-possessive} {self:body-part:" + target + "}.", getAffected(), getOther(), penetrated.describe(getOther()))));
        getAffected().body.pleasure(getOther(), penetrated, stick, 10, c);
        getOther().body.pleasure(getAffected(), stick, penetrated, 2, c);
        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
    }

    public void onRemove(Combat c, Character other) {
        c.write(other, Formatter.format("{other:NAME-POSSESSIVE} slick %s falls off {self:direct-object} with an audible pop.", getAffected(), other, penetrated.describe(other)));
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
    public int escape() {
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
        return Formatter.capitalizeFirstLetter(penetrated.getType()) + " fucked";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new PartSucked(newAffected.getType(), newOther.getType(), penetrated, target);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("penetrator", JsonUtils.getGson().toJson(penetrated));
        obj.addProperty("target", target);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new PartSucked(null, null, JsonUtils.getGson().fromJson(obj.get("penetrator"), BodyPart.class), obj.get("target").getAsString());
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }

    @Override
    public BodyPart getHolePart() {
        return penetrated;
    }

    @Override
    public Character getReceiver() {
        return getOther();
    }

    @Override
    public BodyPart getStickPart() {
        return getAffected().body.getRandom(target);
    }

    @Override
    public Character getPitcher() {
        return getAffected();
    }
}
