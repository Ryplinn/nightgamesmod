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

public class PartFucked extends Status implements InsertedStatus {
    private String target;
    private CharacterType other;
    private BodyPart penetrator;

    public PartFucked(CharacterType affected, CharacterType other, BodyPart stick, String hole) {
        super(Formatter.capitalizeFirstLetter(stick.getType()) + (hole.equals("ass") ? " Pegged" : " Fucked"), affected);
        target = hole;
        this.penetrator = stick;
        this.other = other;
        requirements.add((c, self, opponent) -> c != null && c.getStance().distance() <= 1);
        flag(Stsflag.debuff);
    }

    public Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        BodyPart hole = getAffected().body.getRandom(target);
        if (hole == null || penetrator == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s now fucking %s %s with %s %s\n",
                        getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(), hole.describe(getAffected()),
                        getOther().possessiveAdjective(), penetrator.describe(getOther())));
    }

    @Override
    public String describe(Combat c) {
        BodyPart hole = getAffected().body.getRandom(target);
        if (hole == null || penetrator == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s fucking %s %s with %s %s\n",
                            getOther().subjectAction("are", "is"), getAffected().nameOrPossessivePronoun(),
                            hole.describe(getAffected()), getOther().possessiveAdjective(), penetrator.describe(getOther())));
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
        if (hole == null || penetrator == null || c == null) {
            getAffected().removelist.add(this);
            return;
        }
        c.write(getOther(), Formatter.capitalizeFirstLetter(
                        Formatter.format("{other:name-possessive} %s relentlessly fucks {self:name-do} {self:possessive} {self:body-part:"
                                        + target + "}.", getAffected(), getOther(), penetrator.describe(getOther()))));
        getAffected().body.pleasure(getOther(), penetrator, hole, 10, c);
        getOther().body.pleasure(getAffected(), hole, penetrator, 2, c);
        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
    }

    public void onRemove(Combat c, Character other) {
        c.write(other, Formatter.format("{other:NAME-POSSESSIVE} slick %s slips out of {self:direct-object} with an audible pop.", getAffected(), other, penetrator.describe(other)));
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
        return Formatter.capitalizeFirstLetter(penetrator.getType()) + " fucked";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new PartFucked(newAffected.getType(), opponent.getType(), penetrator, target);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("penetrator", JsonUtils.getGson().toJson(penetrator));
        obj.addProperty("target", target);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new PartFucked(null, null, JsonUtils.getGson().fromJson(obj.get("penetrator"), BodyPart.class), obj.get("target").getAsString());
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
        return penetrator;
    }

    @Override
    public Character getPitcher() {
        return getOther();
    }
}
