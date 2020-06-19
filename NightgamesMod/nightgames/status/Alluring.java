package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Alluring extends DurationStatus {

    /**
     * Default constructor for loading
     */
    public Alluring() {
        this(NPC.noneCharacter().getType());
    }

    public Alluring(CharacterType affected, int duration) {
        super("Alluring", affected, duration);
        flag(Stsflag.alluring);
        flag(Stsflag.purgable);
    }

    public Alluring(CharacterType affected) {
        this(affected, 3);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return Formatter.format("{self:SUBJECT-ACTION:are|is} now alluring.\n", getAffected(), null);
    }

    @Override
    public String describe(Combat c) {
        if (!getAffected().human()) {
            return Formatter.format("{self:SUBJECT-ACTION:look|looks} impossibly beautiful to {other:name-possessive} eyes, {other:pronoun} can't bear to hurt {self:direct-object}.", getAffected(), c.getOpponent(getAffected()));
        }
        return "";
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public float fitnessModifier() {
        return 4.0f;
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
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Alluring(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Alluring(null, obj.get("duration").getAsInt());
    }
}
