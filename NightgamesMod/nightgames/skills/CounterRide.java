package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Cowgirl;
import nightgames.stance.Missionary;

public class CounterRide extends CounterBase {
    CounterRide() {
        super("Sex Counter", 5, "{self:SUBJECT-ACTION:invite|invites} the opponent with {self:possessive} body.");
        addTag(SkillTag.fucking);
        addTag(SkillTag.positioning);
        addTag(SkillTag.counter);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) Random.randomdouble() * 2;
    }

    @Override
    public void resolveCounter(Combat c, Character user, Character target) {
    	if (target.isPet()) {
    		c.write("Something weird happened, pets shouldn't trigger counters.");
    		return;
    	}
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (target.hasDick() && user.hasPussy()) {
            c.setStance(Cowgirl.similarInstance(user, target), user, true);
            new Thrust().resolve(c, user, target, true);
        } else {
            c.setStance(Missionary.similarInstance(user, target), user, true);
            new Thrust().resolve(c, user, target, true);
        }
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 25;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance().dom(user) && !c.getStance().dom(target) && user.canAct()
                        && user.crotchAvailable() && target.crotchAvailable()
                        && (user.hasDick() && target.hasPussy() || user.hasPussy() && target.hasDick()) && target.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 40;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Invites opponent into your embrace";
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup && user.hasPussy()) {
            return Formatter.format(
                            "You turn around and bend over with your ass seductively waving in the air. You slowly "
                                            + "tease your glistening lower lips and spread them apart, inviting {other:name} to take {other:possessive} pleasure.",
                            user, target);
        } else if (modifier == Result.setup && user.hasDick()) {
            return Formatter.format(
                            "You grab your cock and quickly stroke it to full mast. You let your dick go and it swings back and forth, catching {other:name-possessive} gaze.",
                            user, target);
        } else if (user.hasPussy() && target.hasDick()) {
            return Formatter.format(
                            "As {other:subject} approaches you, you suddenly lower your center of balance and sweep {other:possessive} legs out from under her. "
                                            + "With one smooth motion, you drop your hips and lodge {other:possessive} dick firmly inside yourself.",
                            user, target);
        } else {
            return Formatter.format(
                            "As {other:subject} approaches you, you suddenly lower your center of balance and sweep {other:possessive} legs out from under her. "
                                            + "With one smooth motion, you spread her legs apart and plunge into her depths.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup && user.hasPussy()) {
            return Formatter.format(
                            "{self:SUBJECT} turns around and bends over her ass seductively waving in the air. She slowly "
                                            + "teases her glistening lower lips and spread them apart, inviting {other:name-do} in to her depths.",
                            user, target);
        } else if (modifier == Result.setup && user.hasDick()) {
            return Formatter.format(
                            "{self:SUBJECT} takes out her cock and strokes it to full mast. She then lets her dick go and it swings back and forth, catching {other:name-possessive} gaze.",
                            user, target);
        } else if (user.hasPussy() && target.hasDick()) {
            return Formatter.format(
                            "As {other:subject-action:approach|approaches} {self:name}, {self:pronoun} suddenly disappears from "
                            + "{other:possessive} view; half a second later, {other:possessive} legs are swept out from under {other:direct-object}. "
                                            + "With a soft giggle, {self:name} swiftly mounts {other:name-do} and starts riding {other:possessive} cock.",
                            user, target);
        } else {
            return Formatter.format(
                            "As {other:subject} approaches {self:name}, she suddenly disappears from {other:name-possessive} view; half a second "
                            + "later, {other:possessive} legs are swept out from under {other:direct-object}. "
                                            + "With a sexy grin, {self:name} wrenches {other:name-possessive}"
                                            + " legs apart and plunges into {other:possessive} slobbering vagina.",
                            user, target);
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
