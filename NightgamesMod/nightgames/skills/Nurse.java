package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.NursingHold;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Stsflag;
import nightgames.status.Suckling;

public class Nurse extends Skill {

    public Nurse() {
        super("Nurse");
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.breastfeed);
        addTag(SkillTag.usesBreasts);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) > 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.breastsAvailable() && c.getStance().reachTop(user) && c.getStance().front(user)
                        && user.body.getLargestBreasts().getSize() >= BreastsPart.c.getSize()
                        && c.getStance().mobile(user)
                        && (!c.getStance().mobile(target) || c.getStance().prone(target)) && user.canAct();
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        int mod = user.has(Trait.lactating) ? 3 : 0;
        if (user.has(Trait.magicmilk))
            mod *= 3;
        return mod;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean special = c.getStance().en != Stance.nursing && !c.getStance().havingSex(c);
        writeOutput(c, special ? Result.special : Result.normal, user, target);
        if (user.has(Trait.lactating) && !target.is(Stsflag.suckling) && !target.is(Stsflag.wary)) {
            c.write(target, Formatter.format(
                            "{other:SUBJECT-ACTION:are|is} a little confused at the sudden turn of events, but after milk starts flowing into {other:possessive} mouth, {other:pronoun} can't help but continue to suck on {self:possessive} teats.",
                            user, target));
            target.add(c, new Suckling(target.getType(), 4));
        }
        if (special) {
            c.setStance(new NursingHold(user.getType(), target.getType()), user, true);
            new Suckle().resolve(c, user, user);
            user.emote(Emotion.dominant, 20);
        } else {
            new Suckle().resolve(c, user, user);
            user.emote(Emotion.dominant, 10);
        }
        if (Random.random(100) < 5 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), BreastsPart.a.getType(), .25));
        }
        return true;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        if (c.getStance().en != Stance.nursing) {
            return 20;
        } else {
            return 0;
        }
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        if (c.getStance().en != Stance.nursing) {
            return 0;
        } else {
            return 10;
        }
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You cradle " + target.getName() + "'s head in your lap and press your "
                            + user.body.getRandomBreasts().fullDescribe(user) + " over her face. "
                            + target.getName()
                            + " vocalizes a confused little yelp, and you take advantage of it to force your nipples between her lips.";
        } else {
            return "You gently stroke " + target.nameOrPossessivePronoun() + " hair as you feed your nipples to "
                            + target.directObject() + ". " + "Even though she is reluctant at first, you soon have "
                            + target.getName() + " sucking your teats like a newborn.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format("%s plops %s %s in front of %s face. %s vision suddenly consists of only"
                            + " swaying titflesh. Giggling a bit, %s pokes %s sides and slides %s nipples in"
                            + " %s mouth when %s %s out a yelp.", user.subject(),
                            user.possessiveAdjective(), user.body.getRandomBreasts().fullDescribe(user),
                            target.nameOrPossessivePronoun(), Formatter.capitalizeFirstLetter(target.possessiveAdjective()),
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            target.possessiveAdjective(), target.pronoun(), target.action("let"));
        } else {
            return String.format("%s gently strokes %s hair as %s presents her nipples to %s mouth. "
                            + "Presented with the opportunity, %s happily %s on %s breasts.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.pronoun(), target.possessiveAdjective(),
                            target.subject(), target.action("suck"), user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Feed your nipples to your opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
