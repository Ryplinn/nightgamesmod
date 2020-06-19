package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.items.Item;

public class MagLocked extends Status {

    private int count;

    public MagLocked(CharacterType affected) {
        super("MagLocked", affected);
        flag(Stsflag.maglocked);
        count = 1;
    }

    @Override
    public void replace(Status newStatus) {
        if (count < 3)
            count++;
    }

    public void addLock() {
        if (count < 3) count++;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return "";
    }

    @Override
    public String describe(Combat c) {
        if (count == 1) {
            return Formatter.format(
                            "A single inactive MagLock hangs around one of {self:name-possessive}"
                                            + " wrists. It's harmless for now, but any more would be dangerous.",
                            getAffected(), c.getOpponent(getAffected()));
        } else if (count == 2) {
            return Formatter.format(
                            "{other:NAME-POSSESSIVE} two MagLocks, placed around {self:name-possessive}"
                                            + " wrists, have locked together behind {self:possessive} back and"
                                            + " are restraining {self:possessive} movement.",
                            getAffected(), c.getOpponent(getAffected()));
        } else {
            return Formatter.format(
                            "Hogtied by {other:name-possessive} MagLocks,"
                                            + "{self:subject-action:are|is} completely immobilized.",
                            getAffected(), c.getOpponent(getAffected()));
        }
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.speed) {
            return count == 3 ? 99 : count == 2 ? 3 : 0;
        }
        return 0;
    }

    @Override
    public void tick(Combat c) {
        if (count > 1) {
            flag(Stsflag.bound);
            c.getOpponent(getAffected()).consume(Item.Battery, count - 1);
            if (count == 3) {
                flag(Stsflag.hogtied);
            }
        }
        if (!c.getOpponent(getAffected()).has(Item.Battery)) {
            c.write(Formatter.format(
                            "<b>{other:NAME-POSSESSIVE} MagLocks have run out of power and "
                                            + "fall harmlessly off of {self:subject} and onto the ground.",
                            getAffected(), c.getOpponent(getAffected())));
            getAffected().removelist.add(this);
        }
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
        return count == 1 ? -1 : count == 2 ? -4 : -10;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new MagLocked(newAffected.getType());
    }

    @Override
    public JsonObject saveToJson() {
        return null;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return null;
    }

}
