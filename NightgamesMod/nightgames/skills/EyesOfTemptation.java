package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;

public class EyesOfTemptation extends Skill {
    EyesOfTemptation() {
        super("Eyes of Temptation", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.hypnotism) >= 10 || user.getAttribute(Attribute.darkness) >= 15
                        || user.getAttribute(Attribute.spellcasting) >= 20;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && c.getStance()
                                          .facing(user, target)
                        && !user.is(Stsflag.blinded) && !target.wary();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 40;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return target.is(Stsflag.blinded) ? -100 : 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result result = target.is(Stsflag.blinded) ? Result.special
                        : target.roll(user, accuracy(c, user, target)) ? Result.normal : Result.miss;
        writeOutput(c, result, user, target);
        if (result == Result.normal) {
            target.add(c, new Enthralled(target.getType(), user.getType(), 5));
            user.emote(Emotion.dominant, 50);
        }
        return result != Result.miss;
    }

    @Override
    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format(
                            "As {other:subject-action:gaze|gazes} into {self:name-possessive} eyes, {other:subject-action:feel|feels} {other:possessive} will slipping into the abyss.",
                            user, target);
        } else if (modifier == Result.special) {
            if (user.human()) {
                return Formatter.format(
                                "You focus your eyes on {other:name}, but with {other:possessive} eyesight blocked the power just seeps away uselessly.",
                                user, target);
            } else {
                return Formatter.format(
                                "There seems to be a bit of a lull in the fight. {self:SUBJECT-ACTION:are|is} not sure what {other:name} is doing, but it isn't having any effect on {self:direct-object}.",
                                user, target);
            }
        } else {
            return Formatter.format(
                            "{other:SUBJECT-ACTION:look|looks} away as soon as {self:subject-action:focus|focuses} {self:possessive} eyes on {other:direct-object}.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Enthralls your opponent with a single gaze.";
    }
}
