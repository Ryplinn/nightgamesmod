package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.requirements.RequirementShortcuts;

public class ArmLocked extends Status {
    private float toughness;

    /**
     * Default constructor for loading
     */
    public ArmLocked() {
        this(NPC.noneCharacter().getType(), 0);
    }

    public ArmLocked(CharacterType affected, float dc) {
        super("Arm Locked", affected);
        toughness = dc;
        requirements.add(RequirementShortcuts.eitherinserted());
        requirements.add(RequirementShortcuts.dom());
        requirements.add((c, self, other) -> other.canRespond());
        requirements.add((c, self, other) -> toughness > .01);
        flag(Stsflag.armlocked);
    }

    @Override
    public String describe(Combat c) {
        Character opp = c.getOpponent(getAffected());
        return String.format("%s hands are intertwined with %s, preventing %s escape.",
                        Formatter.capitalizeFirstLetter(opp.nameOrPossessivePronoun()), !getAffected().human() && !getAffected().useFemalePronouns()
                        ? "his" : getAffected().possessiveAdjective() + "s", getAffected().possessiveAdjective());
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
        return -Math.round(toughness);
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
        return "Bound by hands";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new ArmLocked(newAffected.getType(), Math.round(toughness));
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("toughness", toughness);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new ArmLocked(null, obj.get("toughness").getAsFloat());
    }
}
