package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

import static nightgames.requirements.RequirementShortcuts.inserted;
import static nightgames.requirements.RequirementShortcuts.rev;

public class Knotted extends Status {

    private CharacterType opponent;
    private boolean anal;

    public Knotted(CharacterType affected, CharacterType other, boolean anal) {
        super("Knotted", affected);
        opponent = other;
        this.anal = anal;
        requirements.add(rev(inserted()));
        flag(Stsflag.knotted);
        flag(Stsflag.purgable);
    }

    public Character getOpponent() {
        return opponent.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("The base of %s %s swells up, forming a tight seal within %s %s and keeping it inside.",
                        getOpponent().nameOrPossessivePronoun(), c.getStance().insertedPartFor(c, getOpponent()).describe(getOpponent()),
                        getAffected().nameOrPossessivePronoun(),
                        c.getStance().insertablePartFor(c, getAffected(), getOpponent()).describe(getAffected()));
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return getOpponent().nameOrPossessivePronoun() + " knotted dick is lodged inside of you, preventing escape.";
        } else {
            return "The knot in " + getOpponent().nameOrPossessivePronoun()
                            + " dick is keeping it fully entrenched within " + getAffected().getName() + ".";
        }
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
        getAffected().emote(Emotion.horny, 20);
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
        return -15;
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
    public int value() {
        return 0;
    }

    @Override
    public float fitnessModifier() {
        // This is counted twice, but that's intentional.
        // (The other place is Character#getFitness())
        return getAffected().body.penetrationFitnessModifier(getAffected(), getOpponent(), false, anal);
    }

    @Override
    public String toString() {
        return "Knotted dick locked inside";
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Knotted(newAffected.getType(), newOther.getType(), anal);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("anal", anal);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Knotted(null, null, obj.get("anal").getAsBoolean());
    }

}
