package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Charmed;
import nightgames.status.Stsflag;

public class Charm extends Skill {
    public Charm() {
        super("Charm", 4);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && c.getStance().facing(user, target) && !(target.is(Stsflag.wary) || target.is(Stsflag.charmed)) ;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        if (isPurr(user)) {
            return 0;
        }
        return 30;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (isPurr(user)) {
            return resolvePurr(c, user, target);
        }
        if (target.human() && target.is(Stsflag.blinded)) {
            printBlinded(c, user);
            return false;
        }
        if (rollSucceeded) {
            writeOutput(c, Result.normal, user, target);
            double mag = 2 + Random.random(4) + user.body.getHotness(target);
            if (target.has(Trait.imagination)) {
                mag += 4;
            }
            int m = (int) Math.round(mag);
            target.temptNoSource(c, user, m, this);
            target.add(c, new Charmed(target.getType()));
            target.emote(Emotion.horny, 10);
            user.emote(Emotion.confident, 20);
        } else {
            writeOutput(c, Result.miss, user, target);
        }
        target.emote(Emotion.horny, 10);
        return true;
    }

    private boolean resolvePurr(Combat c, Character user, Character target) {
        if (Random.random(target.getLevel()) <= user.getAttribute(Attribute.animism) * user.getArousal().percent()
                        / 100 && !target.wary()) {
            int damage = user.getArousal().getReal() / 10;
            if (damage < 10) {
                damage = 0;
            }
            writeOutput(c, damage, Result.special, user, target);
            if (damage > 0) {
                target.temptNoSource(c, user, damage, this);
            }
            target.add(c, new Charmed(target.getType()));
            return true;
        } else {
            writeOutput(c, Result.weak, user, target);
            return false;
        }        
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return (user.getAttribute(Attribute.cunning) >= 8 && user.getAttribute(Attribute.seduction) > 16) || isPurr(user);
    }

    @Override
    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    private boolean isPurr(Character user) {
        return user.getAttribute(Attribute.animism) >= 9 && user.getArousal().percent() >= 20;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You flash a dazzling smile at " + target.directObject() + ", but it wasn't very effective.";
        } else if (modifier == Result.weak){
            return "You let out a soft purr and give " + target.getName()
            + " your best puppy dog eyes. She smiles, but then aims a quick punch at your groin, which you barely avoid. "
            + "Maybe you shouldn't have mixed your animal metaphors.";
        } else if (modifier == Result.special) {
            String message = "You give " + target.getName()
                            + " an affectionate purr and your most disarming smile. Her battle aura melts away and she pats your head, completely taken with your "
                            + "endearing behavior.";
            if (damage > 0) {
                message += "\nSome of your apparent arousal seems to have affected her, her breath seems shallower than before.";
            }
            return message;
        } else {
            return user.getName() + " flashes a dazzling smile at "+target.nameDirectObject()+", charming " + target.directObject() + " instantly";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return user.getName() + " flashes a dazzling smile at "+target.directObject() + ", but it wasn't very effective.";
        } else if (modifier == Result.weak){
            return String.format("%s slumps submissively and purrs. It's cute, but %s's not going "
                            + "to get the better of %s.", user.subject(), user.pronoun(),
                            target.nameDirectObject());
        } else if (modifier == Result.special) {
            String message = String.format("%s purrs cutely, and looks up at %s with sad eyes. Oh God,"
                            + " %s's so adorable! It'd be mean to beat %s too quickly. "
                            + "Maybe %s should let her get some "
                            + "attacks in while %s %s watching %s earnest efforts.",
                            user.subject(), target.nameDirectObject(),
                            user.pronoun(), user.directObject(), target.subject(),
                            target.pronoun(), target.action("enjoy"), user.possessiveAdjective());
            if (damage > 0) {
                message += String.format("\nYou're not sure if this was intentional, but %s flushed "
                                + "face and ragged breathing makes the act a lot more erotic than "
                                + "you would expect. %s to contain %s need to fuck the little kitty in heat.",
                                user.nameOrPossessivePronoun(),
                                Formatter.capitalizeFirstLetter(target.subjectAction("try", "tries")),
                                target.possessiveAdjective());
            }
            return message;
        } else {
            return user.getName() + " flashes a dazzling smile at "+target.nameDirectObject()+", charming " + target.directObject() + " instantly.";
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Charms your opponent into not hurting you.";
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (isPurr(user)) {
            return "Purr";
        }
        return getName(c, user);
    }
}
