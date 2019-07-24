package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

/**
 * A special stun
 */
public class Plasticized extends DurationStatus {
    Plasticized(CharacterType affected) {
        this(affected, 4);
    }

    private Plasticized(CharacterType affected, int duration) {
        super("Plasticized", affected, duration);
        flag(Stsflag.stunned);
        flag(Stsflag.plasticized);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        flag(Stsflag.disabling);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You completely immobilized in a skin of hard plastic.";
        } else {
            return getAffected().getName() + " is completely immobilized in a suit of hard plastic.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (getAffected().human()) {
            return "<b>You are wrapped in a layer of hard plastic and are completely immobilized!</b>";
        } else {
            return "<b>" + getAffected().getName() + " is completely immobilized in a coating of hard plastic!</b>";
        }
    }

    @Override
    public float fitnessModifier() {
        return -40f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        Formatter.writeFormattedIfCombat(c, "{self:SUBJECT-ACTION:are|is} finally freed of {self:possessive} plastic prison!", getAffected(), other);
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        if (c != null && c.getStance().mobile(getAffected())) {
        	c.write(getAffected(), Formatter
                            .format("It's impossible for {self:name-do} to stay on {self:possessive} feet.", getAffected(), c.getOpponent(getAffected())));
        	getAffected().add(c, new Falling(affected));
        }
        getAffected().emote(Emotion.nervous, 5);
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return -x;
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
        return -200;
    }

    @Override
    public int escape(Character from) {
        return -200;
    }

    @Override
    public int gainmojo(int x) {
        return -x;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return -200;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Plasticized(newAffected.getType(), getDuration());
    }

    @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Plasticized(NPC.noneCharacter().getType(), obj.get("duration").getAsInt());
    }
}
