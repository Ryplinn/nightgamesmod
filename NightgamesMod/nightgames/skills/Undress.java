package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.Stsflag;

public class Undress extends Skill {

    public Undress() {
        super("Undress");
        addTag(SkillTag.undressing);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 5;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance()
                                       .sub(user)
                        && (!user.mostlyNude() || !user.reallyNude() && user.stripDifficulty(target) > 0)
                        && !c.getStance()
                             .prone(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Remove your own clothes";
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -10.0f;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result res = Result.normal;
        int difficulty = user.stripDifficulty(target);
        if (difficulty > 0) {
            res = Random.random(50) > difficulty ? Result.weak : Result.miss;
        }

        if (user.human()) {
            c.write(user, deal(c, 0, res, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (target.human() && target.is(Stsflag.blinded))
                printBlinded(c, user);
            else
                c.write(user, receive(c, 0, res, user, target));
        }
        if (res == Result.normal) {
            user.undress(c);
        } else if (res == Result.weak) {
            user.stripRandom(c, true);
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to struggle out of your clothing, but it stubbornly clings onto you.";
        } else if (modifier == Result.weak) {
            return "You manage to struggle out of some of your clothing.";
        }
        if (c.getStance().en != Stance.neutral) {
            return "You wiggle out of your clothes and toss them aside.";
        }
        return "You quickly strip off your clothes and toss them aside.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to struggle out of %s clothing, but it stubbornly clings onto %s.",
                            user.subject(), user.possessiveAdjective(), user.directObject());
        } else if (modifier == Result.weak) {
            return String.format("%s manages to struggle out of some of %s clothing.", user.subject(),
                            user.possessiveAdjective());
        }
        if (c.getStance().en != Stance.neutral) {
            return String.format("%s wiggles out of %s clothes and tosses them aside.", user.subject(),
                            user.possessiveAdjective());
        }
        return String.format("%s puts some space between %s and strips naked.", user.subject(), c.isBeingObserved()
                        ? user.reflectivePronoun() + " and " + target.nameDirectObject() : "you");
    }
}
