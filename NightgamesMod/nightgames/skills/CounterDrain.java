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

public class CounterDrain extends CounterBase {
    CounterDrain() {
        super("Counter Vortex", 6, "{self:SUBJECT-ACTION:glow|glows} with a purple light.");
        addTag(SkillTag.drain);
        addTag(SkillTag.fucking);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
        addTag(SkillTag.dark);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) Random.randomdouble() * 3;
    }

    @Override
    public void resolveCounter(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (target.hasDick() && user.hasPussy()) {
            c.setStance(Cowgirl.similarInstance(user, target), user, true);
        } else {
            c.setStance(Missionary.similarInstance(user, target), user, true);
        }
        Drain drain = new Drain();
        drain.resolve(c, user, target);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.darkness) >= 25;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance().dom(user) && !c.getStance().dom(target) && user.canAct()
                        && user.crotchAvailable() && target.crotchAvailable()
                        && (user.hasDick() && target.hasPussy() || user.hasPussy() && target.hasDick()) && target.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Counter with Drain";
    }

    @Override
    public Skill copy(Character user) {
        return new CounterDrain();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup) {
            return Formatter.format(
                            "You drop your stance, take a deep breath and close your eyes. A purple glow starts radiating from your core.",
                            user, target);
        } else {
            return Formatter.format(
                            "You suddenly open your eyes as you sense {other:name} approaching. "
                                            + "The purple light that surrounds you suddenly flies into {other:direct-object}, "
                                            + "eliciting a cry out of her. She collapses like a puppet with her strings cut and falls to the ground. "
                                            + "Seeing the opportunity, you smirk and leisurely mount her.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup) {
            return Formatter.format(
                            "She drops her stance, takes a deep breath and closes her eyes. {other:SUBJECT-ACTION:notice|notices}"
                            + " a purple glow begin to radiate from her core.",
                            user, target);
        } else {
            return Formatter.format(
                            "{self:SUBJECT} suddenly opens her eyes as {other:subject-action:approach|approaches}. "
                                            + "The purple light that was orbiting around {self:direct-object} suddenly reverses directions and flies into {other:direct-object}. "
                                            + "The purple energy seems to paralyze {other:possessive} muscles and {other:pronoun-action:collapse|collapses}"
                                            + " like a puppet with {other:possessive} strings cut. {other:PRONOUN} can't help but fall to the ground with a cry. "
                                            + "Seeing the opportunity, {self:pronoun} smirks and leisurely mounts {other:direct-object}.",
                            user, target);
        }
    }

    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
