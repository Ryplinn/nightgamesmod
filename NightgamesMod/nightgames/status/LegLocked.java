package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.requirements.RequirementShortcuts;

public class LegLocked extends Status {
    private float toughness;

    public LegLocked(CharacterType affected, float dc) {
        super("Leg Locked", affected);
        requirements.add(RequirementShortcuts.eitherinserted());
        requirements.add(RequirementShortcuts.dom());
        requirements.add((c, self, other) -> toughness > .01);
        requirements.add((c, self, other) -> other.canRespond());
        toughness = dc;
        flag(Stsflag.leglocked);
        flag(Stsflag.debuff);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Her legs are locked around your waist, preventing you from pulling out.";
        } else {
            return String.format("%s legs are wrapped around %s waist, preventing %s from pulling out.",
                            c.getOpponent(getAffected()).nameOrPossessivePronoun(), getAffected().nameOrPossessivePronoun(),
                            getAffected().directObject());
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s being held down.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -toughness / 10.0f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        getAffected().emote(Emotion.horny, 10);
        toughness -= 2;
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
        return -15;
    }

    @Override
    public int escape(Character from) {
        return Math.round(-toughness);
    }

    @Override
    public void struggle(Character self) {
        toughness = Math.round(toughness * .5f);
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
        return "Bound by legs";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new LegLocked(newAffected.getType(), toughness);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("toughness", toughness);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new LegLocked(null, obj.get("toughness").getAsFloat());
    }
}
