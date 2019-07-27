package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.Grab;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Neutral;
import nightgames.status.Compulsive;
import nightgames.status.Compulsive.Situation;
import nightgames.status.Stsflag;

import java.util.Optional;

public class Escape extends Skill {
    public Escape() {
        super("Escape");
        addTag(SkillTag.positioning);
        addTag(SkillTag.escaping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        if (target.hasStatus(Stsflag.cockbound)) {
            return false;
        }
        return (c.getStance()
                 .sub(user)
                        && !c.getStance()
                             .mobile(user)
                        || (user.bound() && !user.is(Stsflag.maglocked))) && user.canRespond();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (blockedByCollar(c, user)) {
            return false;
        }
        if (user.bound()) {
            if (user.checkVsDc(Attribute.cunning, 5 - user.getEscape(c, target))) {
                if (user.human()) {
                    c.write(user, "You slip your hands out of your restraints.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, user.getName() + " manages to free " + user.reflectivePronoun() + ".");
                }
                user.free();
                c.getCombatantData(user).setIntegerFlag(Grab.FLAG, 0);
            } else {
                if (user.human()) {
                    c.write(user, "You try to slip your restraints, but can't get free.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s squirms against %s restraints fruitlessly.", user.getName(),
                                    user.possessiveAdjective()));
                }
                user.struggle();
                return false;
            }
        } else if (user.checkVsDc(Attribute.cunning, 10 + target.getAttribute(Attribute.cunning) - user.getEscape(c, target))) {
            if (user.human()) {
                if (user.hasStatus(Stsflag.cockbound)) {
                    c.write(user, "You somehow managed to wiggle out of " + target.getName()
                                    + "'s iron grip on your dick.");
                    user.removeStatus(Stsflag.cockbound);
                    return true;
                }
                c.write(user, "Your quick wits find a gap in " + target.getName() + "'s hold and you slip away.");
            } else if (c.shouldPrintReceive(target, c)) {
                if (user.hasStatus(Stsflag.cockbound)) {
                    c.write(user,
                                    String.format("%s somehow managed to wiggle out of %s iron grip on %s dick.",
                                                    user.pronoun(), target.nameOrPossessivePronoun(),
                                                    user.possessiveAdjective()));
                    user.removeStatus(Stsflag.cockbound);
                    return true;
                }
                c.write(user, String.format(
                                "%s goes limp and %s the opportunity to adjust %s grip on %s"
                                                + ". As soon as %s %s, %s bolts out of %s weakened hold. "
                                                + "It was a trick!",
                                user.getName(), target.subjectAction("take"),
                                target.possessiveAdjective(), user.directObject(),
                                target.pronoun(), target.action("move"), user.pronoun(),
                                target.possessiveAdjective()));
            }
            c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()), user, true);
        } else {
            c.escape(user, c.getStance());
            user.struggle();
            return false;
        }
        return true;
    }

    private boolean blockedByCollar(Combat c, Character user) {
        Optional<String> compulsion = Compulsive.describe(c, user, Situation.PREVENT_ESCAPE);
        if (compulsion.isPresent()) {
            c.write(user, compulsion.get());
            user.pain(c, null, 20 + Random.random(40));
            Compulsive.doPostCompulsion(c, user, Situation.PREVENT_ESCAPE);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 8;
    }

    @Override
    public int speed(Character user) {
        return 1;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Uses Cunning to try to escape a submissive position";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
