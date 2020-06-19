package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

/**
 * Kind of like a mini-winded.
 */
public class Stunned extends DurationStatus {
    private boolean makesBraced;
    public Stunned(CharacterType affected) {
        this(affected, 1, true);
    }

    public Stunned(CharacterType affected, int duration, boolean makesBraced) {
        super("Stunned", affected, duration);
        flag(Stsflag.stunned);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        flag(Stsflag.disabling);
        this.makesBraced = makesBraced;
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You are stunned!";
        } else {
            return getAffected().getName() + " is stunned!";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now stunned.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -.8f;
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.power || a == Attribute.speed) {
            return -2;
        } else {
            return 0;
        }
    }

    @Override
    public void onRemove(Combat c, Character other) {
        if (makesBraced) {
            if (getAffected().getAttribute(Attribute.divinity) > 0) {
                getAffected().addlist.add(new BastionOfFaith(affected, 3));
            } else {
                getAffected().addlist.add(new Braced(affected, 2));
            }
            getAffected().addlist.add(new Wary(affected, 2));
            getAffected().heal(c, getAffected().getStamina().max() / 3, " (Recovered)");
        }
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.nervous, 15);
        getAffected().emote(Emotion.angry, 10);
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        Formatter.writeIfCombat(c, getAffected(), Formatter.format("Since {self:subject-action:are} already downed, there's not much more that can be done.", getAffected(), getAffected()));
        return -x;
    }

    @Override
    public int weakened(Combat c, int x) {
        Formatter.writeIfCombat(c, getAffected(), Formatter.format("Since {self:subject-action:are} already downed, there's not much more that can be done.", getAffected(), getAffected()));
        return -x;
    }

    @Override
    public int drained(Combat c, int x) {
        Formatter.writeIfCombat(c, getAffected(), Formatter
                        .format("Since {self:subject-action:are} already downed, there's not much to take.", getAffected(), getAffected()));
        return -x;
    }

    @Override
    public int tempted(Combat c, int x) {
        Formatter.writeIfCombat(c, getAffected(), Formatter
                        .format("%s, {self:subject-action:are} already unconscious.", getAffected(), getAffected(), getAffected().human() ? "Fortunately" : "Unfortunately"));
        return -x;
    }

    @Override
    public int evade() {
        return -10;
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
        return -10;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Stunned(newAffected.getType(), getDuration(), makesBraced);
    }

    @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        obj.addProperty("makesBraced", makesBraced);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Stunned(NPC.noneCharacter().getType(), obj.get("duration").getAsInt(), obj.get("makesBraced").getAsBoolean());
    }
}
