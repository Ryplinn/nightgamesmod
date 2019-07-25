package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.stance.Position;
import nightgames.stance.Stance;
import nightgames.status.ArmLocked;
import nightgames.status.LegLocked;
import nightgames.status.Stsflag;

public class SubmissiveHold extends Skill {
    public SubmissiveHold() {
        super("Submissive Hold");
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.submissive) ? 4 : 2;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return (user.get(Attribute.seduction) > 15 && user.get(Attribute.power) >= 15) || user.has(Trait.stronghold);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().sub(user)
                        && user.canSpend(getMojoCost(c, user)) && !target.is(Stsflag.armlocked)
                        && !target.is(Stsflag.leglocked)
                        && c.getStance().havingSex(c, user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Holds your opponent in position";
    }

    private boolean isArmLock(Position p) {
        return p.en != Stance.missionary;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (isArmLock(c.getStance())) {
            return "Hand Lock";
        } else {
            return "Leg Lock";
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (isArmLock(c.getStance())) {
            return Formatter.format("You entwine {other:name-possessive} fingers with your own, holding her in position.",
                            user, target);
        } else {
            return Formatter.format(
                            "You embrace {other:name} and wrap your legs around her waist, holding her inside you.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (isArmLock(c.getStance())) {
            return Formatter.format("{self:SUBJECT} entwines {other:name-possessive} fingers with {self:possessive}"
                            + " own, holding {other:direct-object} in position.",
                            user, target);
        } else {
            return Formatter.format(
                            "{self:SUBJECT} embraces {other:name-do} and wraps {self:possessive} lithesome legs "
                            + "around {other:possessive} waist, holding {other:direct-object} inside {self:direct-object}.",
                            user, target);
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (isArmLock(c.getStance())) {
            target.add(c, new ArmLocked(target.getType(), 4 * user.get(Attribute.power)));
        } else {
            target.add(c, new LegLocked(target.getType(), 4 * user.get(Attribute.power)));
        }
        return true;
    }
}
