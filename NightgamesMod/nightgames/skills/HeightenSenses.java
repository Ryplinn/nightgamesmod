package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.AttributeBuff;
import nightgames.status.Hypersensitive;
import nightgames.status.Stsflag;

public class HeightenSenses extends Skill {

    public HeightenSenses() {
        super("Heighten Senses");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.hypnotism) >= 5;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().behind(user)
                        && !c.getStance().behind(target) && !c.getStance().sub(user)
                        && (!target.is(Stsflag.hypersensitive) || target.getPure(Attribute.perception) < 9);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Hypnotize the target to temporarily become more sensitive";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean alreadyTranced =
                        target.is(Stsflag.charmed) || target.is(Stsflag.enthralled) || target.is(Stsflag.trance);
        if (!alreadyTranced && Random.random(3) == 0) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.miss, user, target));
            } else {
                c.write(user, receive(c, 0, Result.miss, user, target));
            }
            return false;
        } else if (target.is(Stsflag.hypersensitive) && Random.random(2) == 0) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.strong, user, target));
            } else {
                c.write(user, receive(c, 0, Result.strong, user, target));
            }
            target.add(c, new AttributeBuff(target.getType(), Attribute.perception, 1, 20));
        } else {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            target.add(c, new Hypersensitive(target.getType()));
        }
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
                            "You plant a suggestion in %s's head to increase %s sensitivity. %s accepts the suggestion so easily and strongly that you suspect it may have had a permanent effect.",
                            target.getName(), target.possessiveAdjective(), target.pronoun());
        }
        if (modifier == Result.miss) {
            return String.format(
                            "You plant a suggestion in %s's head to increase %s sensitivity. Unfortunately, it didn't seem to affect %s much.",
                            target.getName(), target.possessiveAdjective(), target.directObject());
        }
        return String.format(
                        "You plant a suggestion in %s's head to increase %s sensitivity. %s shivers as %s sense of touch is amplified",
                        user.getName(), target.possessiveAdjective(), target.pronoun(),
                        target.possessiveAdjective());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.strong) {
            return String.format(
                            "%s explains to %s that %s body, especially %s erogenous zones, have become more"
                            + " sensitive. %s's right. All %s senses feel heightened. %s almost like "
                            + "a superhero. It's ok if this is permanent, right?",
                            user.getName(), target.subject(), target.possessiveAdjective(),
                            target.possessiveAdjective(), user.subject(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("feel")));
        }
        if (modifier == Result.miss) {
            return String.format(
                            "%s explains to %s that %s body, especially %s erogenous zones, have become more"
                            + " sensitive. %s aren't really feeling it though.",
                            user.getName(), target.subject(), target.possessiveAdjective(),
                            target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("aren't", "isn't")));
        }
        return String.format(
                        "%s explains to %s that %s body, especially %s erogenous zones, have become more "
                        + "sensitive. %s goosebumps cover %s skin as if %s %s been hit by a "
                        + "Sensitivity Flask. Maybe %s %s and just didn't notice?",
                        user.getName(), target.subject(), target.possessiveAdjective(),
                        target.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                        target.possessiveAdjective(), target.pronoun(), target.action("have", "has"),
                        target.pronoun(), target.action("were", "was"));
    }

}
