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
    public SubmissiveHold(Character self) {
        super("Submissive Hold", self);
    }

    @Override
    public float priorityMod(Combat c) {
        return getSelf().has(Trait.submissive) ? 4 : 2;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return (user.get(Attribute.seduction) > 15 && user.get(Attribute.power) >= 15) || user.has(Trait.stronghold);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().sub(getSelf())
                        && getSelf().canSpend(getMojoCost(c)) && !target.is(Stsflag.armlocked)
                        && !target.is(Stsflag.leglocked)
                        && c.getStance().havingSex(c, getSelf());
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public String describe(Combat c) {
        return "Holds your opponent in position";
    }

    private boolean isArmLock(Position p) {
        if (p.en == Stance.missionary) {
            return false;
        }
        return true;
    }

    @Override
    public String getLabel(Combat c) {
        if (isArmLock(c.getStance())) {
            return "Hand Lock";
        } else {
            return "Leg Lock";
        }
    }

    @Override
    public Skill copy(Character user) {
        return new SubmissiveHold(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (isArmLock(c.getStance())) {
            return Formatter.format("You entwine {other:name-possessive} fingers with your own, holding her in position.",
                            getSelf(), target);
        } else {
            return Formatter.format(
                            "You embrace {other:name} and wrap your legs around her waist, holding her inside you.",
                            getSelf(), target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (isArmLock(c.getStance())) {
            return Formatter.format("{self:SUBJECT} entwines {other:name-possessive} fingers with {self:possessive}"
                            + " own, holding {other:direct-object} in position.",
                            getSelf(), target);
        } else {
            return Formatter.format(
                            "{self:SUBJECT} embraces {other:name-do} and wraps {self:possessive} lithesome legs "
                            + "around {other:possessive} waist, holding {other:direct-object} inside {self:direct-object}.",
                            getSelf(), target);
        }
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        }
        if (isArmLock(c.getStance())) {
            target.add(c, new ArmLocked(target, 4 * getSelf().get(Attribute.power)));
        } else {
            target.add(c, new LegLocked(target, 4 * getSelf().get(Attribute.power)));
        }
        return true;
    }
}
