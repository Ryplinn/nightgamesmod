package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Anal;
import nightgames.stance.Cowgirl;
import nightgames.stance.Missionary;
import nightgames.status.Shamed;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Offer extends Skill {

    Offer() {
        super("Offer");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return (target.pantsless() || target.has(Trait.strapped)) && c.getStance().mobile(target)
                        && !c.getStance().mobile(user)
                        && (target.hasDick() || target.has(Trait.strapped) || target.hasPussy() && user.hasDick())
                        && user.canAct() && target.canAct() && !c.getStance().inserted();
    }

    @Override
    public String describe(Combat c, Character user) {
        Character other = c.getOpponent(user);
        return other.hasDick() || other.has(Trait.strapped)
                        ? "Offer your " + (user.hasPussy() ? "pussy" : "ass") + " to " + other.possessiveAdjective()
                                        + "'s " + other.body.getRandomInsertable().describe(other)
                        : "Offer " + other.directObject() + " the use of your dick";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.getArousal().get() < 15) {
            writeOutput(c, Result.miss, user, target);
            user.add(c, new Shamed(user.getType()));
            if (target.hasDick() || target.has(Trait.strapped)) {
                new Spank().resolve(c, user, user);
            }
            return false;
        }
        if (target.hasDick() || target.has(Trait.strapped)) {
            if (user.hasPussy()) {
                // offer pussy to dick/strapon
                writeOutput(c, Result.special, user, target);
                c.setStance(new Missionary(target.getType(), user.getType()), target, true);
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomPussy(),
                                Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(),
                                Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));

            } else {
                // offer ass to dick/strapon
                writeOutput(c, Result.anal, user, target);
                c.setStance(new Anal(target.getType(), user.getType()), target, true);
                user.body.pleasure(target, target.body.getRandomInsertable(), user.body.getRandomAss(),
                                Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));
                if (!target.has(Trait.strapped)) {
                    target.body.pleasure(user, user.body.getRandomAss(), target.body.getRandomCock(),
                                    Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));
                }
            }
        } else {
            assert user.hasDick() && target.hasPussy();
            // Offer cock to female
            writeOutput(c, Result.normal, user, target);
            c.setStance(new Cowgirl(target.getType(), user.getType()), target, true);
            user.body.pleasure(target, target.body.getRandomPussy(), user.body.getRandomCock(),
                            Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));
            target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomPussy(),
                            Random.random(5) + user.get(Attribute.perception), c, new SkillUsage<>(this, user, target));
        }

        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL,
                            target, Addiction.LOW_INCREASE, c);
            c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case miss:
                if (target.hasDick() || target.has(Trait.strapped)) {
                    return String.format(
                                    "You get on all fours and offer your %s to %s, but %s merely "
                                                    + "chuckles at your meekness. Before you can get back up in shame,"
                                                    + " %s gives you a very satisfying slap on your ass for your troubles.",
                                    user.hasPussy() ? "pussy" : "ass", target.getName(), target.pronoun(), user.pronoun());
                } else {
                    return String.format("You wave your %s at %s, but %s ignores you completely.",
                                    user.body.getRandomCock().describe(user), target.getName(),
                                    target.pronoun());
                }
            case normal:
                return String.format(
                                "You lay flat on your back with your legs together and holding your %s "
                                                + "straight up with your hand, all ready for %s to mount. %s weighs the situation for only"
                                                + " a brief moment before sitting down on your awaiting shaft.",
                                user.body.getRandomCock().describe(user), target.getName(),
                                Formatter.capitalizeFirstLetter(target.pronoun()));
            case anal:
                return String.format(
                                "You get on the ground with "
                                                + "your shoulders on the ground and your ass in the air, pointing towards %s."
                                                + " Reaching back, you spread your butt and softly whimper an invitation for %s"
                                                + " to stick %s %s into your ass. %s takes pity on you, and plunges in.",
                                target.getName(), target.directObject(), target.possessiveAdjective(),
                                target.body.getRandomInsertable().describe(target),
                                Formatter.capitalizeFirstLetter(target.pronoun()));
            default: // special
                return String.format(
                                "You lay down on your back and spread your legs,"
                                                + "offering your %s to %s while gazing up at %s powerful %s, hoping "
                                                + "that %s will soon fill you with it.",
                                user.body.getRandomPussy().describe(user), target.getName(),
                                target.possessiveAdjective(), target.body.getRandomInsertable().describe(target),
                                target.pronoun());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case miss:
                if (target.hasDick() || target.has(Trait.strapped)) {
                    return String.format(
                                    "%s gets down and sticks %s ass in the air, offering it up to %s. "
                                                    + "%s not interested, however, and just %s %s. %s seemed to enjoy that, but"
                                                    + " is still disappointed over not getting the fucking %s wanted.",
                                    user.getName(), user.possessiveAdjective(), target.nameDirectObject(),
                                    Formatter.capitalizeFirstLetter(target.subjectAction("are","is")),
                                    target.action("spank"), user.directObject(),
                                    Formatter.capitalizeFirstLetter(user.pronoun()), user.pronoun());
                } else {
                    return String.format(
                                    "%s grabs %s %s and waves it at %s, "
                                                    + "trying to entice %s to mount %s. %s just %s at %s pathetic display, "
                                                    + "destroying %s confidence.",
                                    user.getName(), user.possessiveAdjective(),
                                    user.body.getRandomCock().describe(user),
                                    target.nameDirectObject(), target.dickPreference(), user.directObject(),
                                    Formatter.capitalizeFirstLetter(target.subject()), target.action("laugh"),
                                    user.possessiveAdjective(), user.possessiveAdjective());
                }
            case normal:
                return String.format(
                                "%s gets onto %s back and holds %s %s up for %s appraisal."
                                                + " %s to %s that it does seem rather appealing, and %s"
                                                + " to mount %s, enveloping the hard shaft in %s %s.",
                                user.getName(), user.possessiveAdjective(), user.possessiveAdjective(),
                                user.body.getRandomCock().describe(user), target.nameOrPossessivePronoun(),
                                Formatter.capitalizeFirstLetter(target.subjectAction("admit")), target.reflectivePronoun(),
                                target.action("proceed"), user.directObject(), target.possessiveAdjective(),
                                target.body.getRandomPussy().describe(target));
            case anal:
                return String.format(
                                "%s gets on the ground and spreads %s ass for %s viewing pleasure,"
                                                + " practically begging %s to fuck %s. Well, someone has to do it. %s on %s"
                                                + " knees and %s to it.",
                                user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                                target.directObject(), user.directObject(),
                                Formatter.capitalizeFirstLetter(target.subjectAction("get")),
                                target.possessiveAdjective(), target.action("get"));
            default: // special
                return String.format(
                                "%s lays flat on %s back, spreading %s legs and then using"
                                                + " %s fingers to open up %s labia to %s. %s beady eyes, staring longingly"
                                                + " at %s %s overwhelm %s, and %s can't help but oblige, getting "
                                                + "between %s legs and sheathing %s shaft.",
                                user.getName(), user.possessiveAdjective(), user.possessiveAdjective(),
                                user.possessiveAdjective(), user.possessiveAdjective(),
                                target.nameDirectObject(),
                                Formatter.capitalizeFirstLetter(user.possessiveAdjective()), target.possessiveAdjective(),
                                target.body.getRandomCock().describe(target), target.directObject(),
                                target.pronoun(), user.possessiveAdjective(), user.possessiveAdjective());
        }
    }
}
