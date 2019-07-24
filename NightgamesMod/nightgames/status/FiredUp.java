package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class FiredUp extends DurationStatus {

    private int stack;
    private CharacterType other;
    private String part;

    public FiredUp(CharacterType affected, CharacterType other, String part) {
        super("Fired Up", affected, 2);
        this.part = part;
        stack = 1;
        this.other = other;
        flag(Stsflag.firedup);
        flag(Stsflag.purgable);
    }

    private Character getOther() {
        return other.fromPoolGuaranteed();
    }

    public int getStack() {
        return stack;
    }

    public String getPart() {
        return part;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s really getting into using %s %s.", getAffected().subjectAction("are", "is"),
                        getAffected().possessiveAdjective(), part);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            if (stack == 1) {
                return "You are getting a good sense of how to best use your " + part + ".";
            } else if (stack == 2) {
                return String.format("The movements of your %s are growing ever more attuned to %s reactions.", part,
                                getOther().nameOrPossessivePronoun());
            } else {
                return String.format(
                                "You have completely mapped out %s body, and you are finding all of %s most sensitive areas as if by magic.",
                                getAffected().nameOrPossessivePronoun(), getAffected().possessiveAdjective());
            }
        } else {
            if (stack == 1) {
                return Formatter.capitalizeFirstLetter(String.format(
                                "%s has a big grin on %s face at the prospect of further pleasuring %s with %s %s.",
                                getAffected().pronoun(), getAffected().possessiveAdjective(), c.getOpponent(getAffected()).nameDirectObject(),
                                getAffected().possessiveAdjective(), part));
            } else if (stack == 2) {
                return Formatter.capitalizeFirstLetter(String.format(
                                "%s looks as if %s is enjoying working %s %s almost as much as %s.",
                                getAffected().pronoun(), getAffected().possessiveAdjective(), getAffected().pronoun(), part,
                                c.getOpponent(getAffected()).subjectAction("are", "is")));
            } else {
                return Formatter.capitalizeFirstLetter(
                                String.format("%s is focused almost exclusively on using %s %s to the greatest possible effect, and it's working.",
                                                getAffected().pronoun(), getAffected().possessiveAdjective(), part));
            }
        }
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
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
    public float fitnessModifier() {
        return 5 * stack;
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
        return stack * 2;
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
    public Status instance(Character newAffected, Character newOther) {
        return new FiredUp(newAffected.getType(), newOther.getType(), part);
    }

    @Override
    public boolean overrides(Status s) {
        // Replace only if it's the same part.
        return s instanceof FiredUp && !((FiredUp) s).part.equals(part);
    }

    @Override
    public void replace(Status s) {
        assert s instanceof FiredUp;
        stack++;
        setDuration(2);
    }

     @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("part", part);
        obj.addProperty("stack", stack);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        FiredUp fu = new FiredUp(null, null, obj.get("part").getAsString());
        fu.stack = obj.get("stack").getAsInt();
        return fu;
    }

}
