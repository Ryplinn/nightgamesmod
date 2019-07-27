package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.status.Horny;
import nightgames.status.Stsflag;

public class LewdSuggestion extends Skill {

    public LewdSuggestion() {
        super("Lewd Suggestion");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.hypnotism) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().behind(user)
                        && !c.getStance().behind(target) && !c.getStance().sub(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Plant an erotic suggestion in your hypnotized target.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean alreadyTranced =
                        target.is(Stsflag.charmed) || target.is(Stsflag.enthralled) || target.is(Stsflag.trance);
        if (!alreadyTranced && Random.random(3) == 0) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.miss, user, target));
            } else {
                c.write(user, receive(c, 0, Result.miss, user, target));
            }
            return false;
        } else if (target.is(Stsflag.horny)) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.strong, user, target));
            } else {
                c.write(user, receive(c, 0, Result.strong, user, target));
            }
        } else if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }

        target.add(c, Horny.getWithPsychologicalType(user, target, 10, 4, "Hypnosis"));
        target.emote(Emotion.horny, 30);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.strong) {
            return String.format(
                            "You take advantage of the erotic fantasies already swirling through %s's head, whispering ideas that fan the flame of %s lust.",
                            target.getName(), target.possessiveAdjective());
        }
        if (modifier == Result.miss) {
            return String.format(
                            "You whisper ideas that attempt to fan the flame of %s lust, but it doesn't seem to do much",
                            target.nameOrPossessivePronoun());
        }
        return String.format("You plant an erotic suggestion in %s's mind, distracting %s with lewd fantasies.", target.getName(),
                        target.directObject());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.strong) {
            return String.format(
                            "%s whispers a lewd suggestion to %s, intensifying the fantasies %s %s trying to ignore and inflaming %s arousal.",
                            user.getName(), target.nameDirectObject(), target.pronoun(), target.action("were", "was"),
                            target.possessiveAdjective());
        }
        if (modifier == Result.miss) {
            return String.format(
                            "%s whispers a lewd suggestion to %s, but %s just %s it, and %s to concentrate on the fight.",
                            user.getName(), target.nameDirectObject(), target.pronoun(),
                            target.action("ignore"), target.action("try", "tries"));
        }
        return String.format(
                        "%s gives %s a hypnotic suggestion and %s head is immediately filled with erotic possibilities.",
                        user.getName(), target.nameDirectObject(), target.possessiveAdjective());
    }

}
