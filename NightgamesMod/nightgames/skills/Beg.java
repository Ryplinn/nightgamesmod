package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Charmed;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Beg extends Skill {

    public Beg() {
        super("Beg");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance()
                                       .dom(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Beg your opponent to go easy on you";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if ((Random.random(30) <= user.getAttribute(Attribute.submission) - target.getAttribute(Attribute.cunning) / 2
                        && !target.is(Stsflag.cynical) || target.getMood() == Emotion.dominant)
                        && target.getMood() != Emotion.angry && target.getMood() != Emotion.desperate) {
            Result results;
            if (user.is(Stsflag.fluidaddiction)) {
                results = Result.special;
            } else {
                results = Result.normal;
            }
            if (user.human()) {
                c.write(user, deal(c, 0, results, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, receive(c, 0, results, user, target));
            }
            if (results == Result.normal) {
                target.add(c, new Charmed(target.getType()));
            }
            if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
                user.unaddictCombat(AddictionType.MIND_CONTROL, target, Addiction.LOW_INCREASE, c);
                c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
            }
            return true;
        }
        writeOutput(c, Result.miss, user, target);
        return false;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You throw away your pride and ask " + target.getName() + " for mercy. This just seems to encourage "
                            + target.possessiveAdjective() + " sadistic side.";
        }
        if (modifier == Result.special) {
            return Formatter.format("You put yourself completely at {other:name-possessive} mercy and beg for some more of addictive fluids. "
                            + "Unfortunately {other:pronoun} doesn't seem to be very inclined to oblige you.", user, target);
        }
        return "You put yourself completely at " + target.getName() + "'s mercy. "
                        + Formatter.capitalizeFirstLetter(target.pronoun())
                        + " takes pity on you and gives you a moment to recover.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s gives %s a pleading look and asks %s to go light on %s."+
                            "%s is cute, but %s is not getting away that easily.", user.getName(), target.subject(),
                            target.directObject(), user.directObject(), Formatter.capitalizeFirstLetter(user.pronoun()),
                            user.pronoun());
        }
        if (modifier == Result.special) {
            return user.getName() + " begs you for a taste of your addictive fluids, looking almost ready to cry. Maybe you should give "
                            + user.directObject() + " a break...?";
        }
        return user.getName() + " begs you for mercy, looking ready to cry. Maybe you should give "
                        + user.directObject() + " a break.";

    }

}
