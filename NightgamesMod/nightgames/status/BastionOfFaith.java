package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

import java.util.Arrays;
import java.util.List;

public class BastionOfFaith extends DurationStatus {

    BastionOfFaith(CharacterType affected) {
        this(affected, 6);
    }

    BastionOfFaith(CharacterType affected, int duration) {
        super("Bastion of Faith", affected, duration);
        flag(Stsflag.braced);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s divine protection.\n", getAffected().subjectAction("have", "has"));
    }

    @Override
    public String describe(Combat c) {
        return String.format("%s protected by a divine aura.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return (10.0f + 10.0f * getDuration()) / 40.f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        List<String> possibleStrings = Arrays.asList(
                        "{self:NAME-POSSESSIVE} holy barrier makes it impossible to damage {self:direct-object}.",
                        "{self:NAME-POSSESSIVE} holy barrier is making it impossible to damage {self:direct-object}.",
                        "A golden barrier surrounding {self:name-do} is making it impossible to damage {self:direct-object}."
                        );
        Formatter.writeIfCombat(c, getAffected(), Formatter
                        .format(Random.pickRandomGuaranteed(possibleStrings), getAffected(), getAffected()));
        return -x;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        List<String> possibleStrings = Arrays.asList(
                        "{self:NAME-POSSESSIVE} holy barrier is re-energizing {self:direct-object}.",
                        "{self:NAME-POSSESSIVE} holy barrier is buoying up {self:possessive} stamina."
                        );
        Formatter.writeIfCombat(c, getAffected(), Formatter
                        .format(Random.pickRandomGuaranteed(possibleStrings), getAffected(), getAffected()));
        return -x;
    }

    @Override
    public int drained(Combat c, int x) {
        List<String> possibleStrings = Arrays.asList(
                        "{self:NAME-POSSESSIVE} holy barrier is preventing {self:direct-object} from being drained.",
                        "{self:NAME-POSSESSIVE} holy barrier prevents {self:direct-object} draining effects.",
                        "A golden barrier surrounding {self:name-do} stops the theft of {self:possessive} stamina."
                        );
        Formatter.writeIfCombat(c, getAffected(), Formatter
                        .format(Random.pickRandomGuaranteed(possibleStrings), getAffected(), getAffected()));
        return -x;
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
    public int value() {
        return 30 + 30 * getDuration();
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new BastionOfFaith(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new BastionOfFaith(null);
    }
}
