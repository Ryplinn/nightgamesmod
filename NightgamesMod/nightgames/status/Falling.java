package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.stance.StandingOver;

public class Falling extends Status {
    public Falling(CharacterType affected) {
        super("Falling", affected);
        flag(Stsflag.falling);
        flag(Stsflag.debuff);
    }

    @Override
    public String describe(Combat c) {
        return "";
    }

    @Override
    public float fitnessModifier() {
        return -20;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s knocked off balance.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public int regen(Combat c) {
        getAffected().removelist.add(this);
        if (c.getStance().havingSex(c) && c.getStance().dom(getAffected()) && c.getStance().reversable(c)) {
            c.write(c.getOpponent(getAffected()), Formatter.format("{other:SUBJECT-ACTION:take|takes} the chance to shift into a more dominant position.", getAffected(), c.getOpponent(getAffected())));
            c.setStance(c.getStance().reverse(c, true));
        } else if (!c.getStance().prone(getAffected())) {
            c.setStance(new StandingOver(c.getOpponent(getAffected()), getAffected()));
        }
        if (getAffected().has(Trait.NimbleRecovery)) {
            c.write(Formatter.format("{self:NAME-POSSESSIVE} nimble body expertly breaks the fall.", getAffected(), c.getOpponent(getAffected())));
            getAffected().add(c, new Stunned(affected, 0, true));
        } else if (getAffected().has(Trait.Unwavering)) {
            c.write(Formatter.format("{self:SUBJECT-ACTION:go|goes} down but the fall seems to hardly affect {self:direct-object}.", getAffected(), c.getOpponent(getAffected())));
        } else {
            getAffected().add(c, new Stunned(affected));
        }
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
        return 0;
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
        return 0;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Falling(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Falling(null);
    }
}
