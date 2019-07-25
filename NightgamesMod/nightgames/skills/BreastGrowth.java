package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Hypersensitive;

public class BreastGrowth extends Skill {
    BreastGrowth() {
        super("Breast Growth");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.spellcasting) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance()
                                      .mobile(user)
                        && !c.getStance()
                             .prone(user);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 0;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Grow your opponent's boobs to make her more sensitive.";
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result res;
        if (target.roll(user, accuracy(c, user, target))) {
            if (target.body.getRandomBreasts().equals(BreastsPart.flat)) {
                res = Result.special;
            } else {
                res = Result.normal;
            }
        } else {
            res = Result.miss;
        }
        boolean permanent = Random.random(20) == 0 && (user.human() || c.shouldPrintReceive(target, c))
                        && !target.has(Trait.stableform);
        writeOutput(c, permanent ? 1 : 0, res, user, target);
        if (res != Result.miss) {
            target.add(c, new Hypersensitive(target.getType(), 10));
            BreastsPart part = target.body.getBreastsBelow(BreastsPart.f.getSize());
            if (permanent) {
                if (part != null) {
                    target.body.addReplace(part.upgrade(), 1);
                    target.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade().upgrade(), 10);
                }
            } else {
                if (part != null) {
                    target.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade().upgrade(), 10);
                }
            }
        }
        return res != Result.miss;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        if (modifier == Result.normal) {
            message = String.format(
                            "You channel your arcane energies into %s breasts, "
                                            + "causing them to grow rapidly. %s knees buckle with the new"
                                            + " sensitivity you bestowed on %s boobs.",
                            target.nameOrPossessivePronoun(),
                            Formatter.capitalizeFirstLetter(target.possessiveAdjective()), target.possessiveAdjective());
            if (damage > 0) {
                message += " You realize the effects are permanent!";
            }
        } else if (modifier == Result.special) {
            message = String.format(
                            "You channel your arcane energies into %s flat chest, "
                                            + "causing small mounds to rapidly grow on %s. %s knees buckle with the"
                                            + " sensitivity you bestowed on %s new boobs.",
                            target.nameOrPossessivePronoun(), target.directObject(),
                            Formatter.capitalizeFirstLetter(target.possessiveAdjective()), target.possessiveAdjective());
            if (damage > 0) {
                message += " You realize the effects are permanent!";
            }
        } else {
            message = String.format(
                            "You attempt to channel your arcane energies into %s breasts, but "
                                            + "%s %s out of the way, causing your spell to fail.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.action("dodge"));
        }
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        if (modifier == Result.normal) {
            message = String.format(
                            "%s moving and begins chanting. %s %s breasts grow hot, and they start expanding!"
                                            + " %s to hold them back with %s hands, but the growth continues untill they are a full cup size"
                                            + " bigger than before. The new sensations from %s substantially larger breasts make %s tremble.",
                            user.getName(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                            target.possessiveAdjective(), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("try", "tries"), target.possessiveAdjective(), target.directObject());
            if (damage > 0) {
                message += Formatter.capitalizeFirstLetter(target.subjectAction("realize"))
                                + " the effects are permanent!";
            }
        } else if (modifier == Result.special) {
            message = String.format(
                            "%s moving and begins chanting. %s %s chest grow hot, and small, perky breasts start to form!"
                                            + " %s to hold them back with %s hands, but the growth continues untill they are a full A-cup."
                                            + " The new sensations from %s new breasts make %s tremble.",
                            user.getName(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                            target.possessiveAdjective(), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("try", "tries"), target.possessiveAdjective(), target.directObject());
        } else {
            message = String.format(
                            "%s moving and begins chanting. %s feeling some tingling in %s breasts, "
                                            + "but it quickly subsides as %s %s out of the way.",
                            user.subjectAction("stop"),
                            Formatter.capitalizeFirstLetter(target.subjectAction("start")), target.possessiveAdjective(),
                            target.pronoun(), target.action("dodge"));
        }
        return message;
    }

}
