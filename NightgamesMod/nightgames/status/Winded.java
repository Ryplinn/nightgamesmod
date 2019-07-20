package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Winded extends DurationStatus {
    // TODO: Why different flags for these constructors?
    public Winded(CharacterType affected) {
        this(affected, 3);
        flag(Stsflag.disabling);
    }

    public Winded(CharacterType affected, int duration) {
        super("Winded", affected, duration);
        flag(Stsflag.stunned);
        flag(Stsflag.purgable);
        flag(Stsflag.debuff);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You need a moment to catch your breath";
        } else {
            return getAffected().getName() + " is panting and trying to recover";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now winded.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public float fitnessModifier() {
        return -.3f;
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
        if (c != null) {
            if (c.getStance().mobile(getAffected())) {
                if (getAffected().get(Attribute.divinity) > 0) {
                    getAffected().addlist.add(new BastionOfFaith(affected));
                } else {
                    getAffected().addlist.add(new Braced(affected));
                }
            }
            getAffected().addlist.add(new Wary(affected, 3));
            getAffected().heal(c, getAffected().getStamina().max(), " (Recovered)");
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
    public int escape() {
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
    public Status instance(Character newAffected, Character newOther) {
        return new Winded(newAffected.getType());
    }

    @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        //Winded constructor can't handle nulls
        throw new UnsupportedOperationException();
        //return new Winded(null);
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }
}
