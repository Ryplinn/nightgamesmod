package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Falling;
import nightgames.status.Slimed;
import nightgames.utilities.MathUtils;

public class Trip extends Skill {
    public Trip() {
        super("Trip", 2);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance().mobile(user) && !c.getStance().prone(target) && c.getStance().front(user)
                        && user.canAct();
    }

    private boolean isSlime(Character user) {
        return user.get(Attribute.slime) > 11;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            if (isSlime(user)) {
                writeOutput(c, Result.special, user, target);
                if (user.has(Trait.VolatileSubstrate)) {
                    target.add(c, new Slimed(target.getType(), user.getType(), Random.random(2, 4)));
                }
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            target.add(c, new Falling(target.getType()));
        } else {
            if (isSlime(user)) {
                writeOutput(c, Result.weak, user, target);
            } else {
                writeOutput(c, Result.miss, user, target);
            }
            return false;
        }
        return true;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 16;
    }

    @Override
    public Skill copy(Character user) {
        return new Trip();
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        double cunningDifference = user.get(Attribute.cunning) - c.getOpponent(user)
                                                                       .get(Attribute.cunning);
        double accuracy = 2.5f * cunningDifference + 75 - target.knockdownDC();
        if (isSlime(user)) {
            accuracy += 25;
        }

        return (int) Math.round(MathUtils.clamp(accuracy, isSlime(user) ? 50 : 25, 150));
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to trip " + target.getName() + ", but she keeps her balance.";
        } else if (modifier == Result.special) {
            return String.format(
                            "You reshape your hands into a sheet of slime and slide it under %s's feet."
                                            + " When you quickly pull it back, %s falls flat on %s back.",
                            target.getName(), target.pronoun(), target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format(
                            "You reshape your hands into a sheet of slime and slide it towards %s."
                                            + " In the nick of time, %s jumps clear, landing safely back on %s feet.",
                            target.getName(), target.pronoun(), target.possessiveAdjective());
        } else {
            return "You catch " + target.getName() + " off balance and trip " + target.directObject() + ".";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s hooks %s ankle, but %s %s without falling.", user.subject(),
                            target.nameOrPossessivePronoun(), target.pronoun(), target.action("recover"));
        } else if (modifier == Result.special) {
            return String.format(
                            "%s shoves a mass of %s slime under %s feet, destabilizing %s. With a few"
                                            + " pulls, %s throws %s onto %s back.",
                            user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.directObject(), user.pronoun(), target.directObject(),
                            target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format(
                            "%s forms some of %s slime into a sheet and slides it towards %s feet."
                                            + " %s %s away from it, and %s harmlessly retracts the slime.",
                            user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            Formatter.capitalizeFirstLetter(target.pronoun()), target.action("jump"), user.pronoun());
        } else {
            return String.format("%s takes %s feet out from under %s and sends %s sprawling to the floor.",
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject(),
                            target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attempt to trip your opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
