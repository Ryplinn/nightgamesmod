package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.FlowerSex;

public class CounterFlower extends CounterBase {
    CounterFlower() {
        super("Flower Counter", 5,
                        "<b>The giant flower at the base of {self:name-possessive} legs are open, with the petals waving invitingly.</b>",
                        2);
        addTag(SkillTag.fucking);
        addTag(SkillTag.positioning);
        addTag(SkillTag.counter);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) Random.randomdouble() * 2;
    }

    @Override
    public int speed(Character user) {
        return -20;
    }

    @Override
    public String getBlockedString(Combat c, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT-ACTION:block|blocks} {other:name-possessive} assault with a vine and {self:action:shoot|shoots} out {self:possessive} vines to drag {other:direct-object} into {self:possessive} flower. "
                                        + "However, {other:subject-action:were|was} wary of {self:direct-object} and {other:action:jump|jumps} back before {self:subject} can catch {other:direct-object}.",
                        user, target);
    }

    @Override
    public void resolveCounter(Combat c, Character user, Character target) {
        target.nudify();
        if (target.hasDick() && user.hasPussy() && !target.isPet()) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            c.setStance(new FlowerSex(user.getType(), target.getType()), user, true);
            new Thrust().resolve(c, user, target);
        } else {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.miss, user, target));
            } else {
                c.write(user, receive(c, 0, Result.miss, user, target));
            }
        }
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.bio) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.has(Trait.dryad) && !c.getStance().dom(user) && !c.getStance().dom(target) && user.canAct() && user.hasPussy()
                        && target.hasDick() && target.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 40;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Counters with vines, trapping them in your flower.";
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup) {
            return Formatter.format("You open up the flower at the base of your legs and get ready for a counter.",
                            user, target);
        } else if (modifier == Result.miss) {
            return Formatter.format(
                            "You shoot out your vines and drag {other:name-do} into your flower. You urge {other:possessive} hips forward into yours, but "
                                            + "you discover that you do not have the right equipment for the job. Whoops!",
                            user, target);
        } else {
            return Formatter.format(
                            "You shoot out your vines and drag {other:name-do} into your flower. You shove {other:possessive} face between your breasts "
                                            + "and {other:possessive} cock inside your drenched flower cunt. "
                                            + "With a quick flick of your mind, you close the petals of your outer flower around yourselves, trapping {other:name} and you inside."
                                            + "",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup) {
            return Formatter.format(
                            "{self:SUBJECT} giggles softly and opens the flower at the base of {self:possessive} legs invitingly.",
                            user, target);
        } else if (modifier == Result.miss) {
            return Formatter.format(
                            "Numerous vines shoot out of her flower, entangling your body and stopping you in your tracks. "
                            + "With a salacious smile, {self:subject} uses her vines and drags {other:name-do} into {self:possessive} flower and deposits you in {self:possessive} arms. "
                            + " {self:PRONOUN} forces {other:possessive} hips forward before frowning"
                            + " when she discovers {other:pronoun-action:don't|doesn't} have the right equipment.",
                            user, target);
        } else {
            return Formatter.format(
                            "Numerous vines shoot out of her flower, entangling your body and stopping you in your tracks. "
                            + "With a salacious smile, {self:subject} uses her vines and drags {other:name-do} into {self:possessive} flower and deposits you in {self:possessive} arms. "
                            + "{self:PRONOUN} coils her limbs around {other:possessive}s, forcing {other:possessive}"
                            + " face inside her fragrant cleavage and {other:possessive} cock inside her warm sticky flower cunt.",
                            user, target);
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
