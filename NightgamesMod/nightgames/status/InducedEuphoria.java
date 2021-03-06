package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class InducedEuphoria extends DurationStatus {
    public InducedEuphoria(CharacterType affected) {
        super("Induced Euphoria", affected, 20);
        flag(Stsflag.aphrodisiac);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your entire body is flushed with chemically induced pleasure. Every sensation turns you on.";
        } else {
            return getAffected().possessiveAdjective()
                            + " body is alight with chemically induced euphoria. Every sensation causes "
                            + getAffected().pronoun() + " to moan softly.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now affected by an aphrodisiac.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -10;
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
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return x / 2;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return x / 2;
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

    public boolean lingering() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new InducedEuphoria(newAffected.getType());
    }

     public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    public Status loadFromJson(JsonObject obj) {
        return new InducedEuphoria(null);
    }
}
