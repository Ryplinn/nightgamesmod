package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;

public class Piston extends Thrust {
    public Piston() {
        super("Piston");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target) && (c.getStance().canthrust(c, user) || user.has(Trait.powerfulhips));
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] results = new int[2];

        int m = 15 + Random.random(8);
        int mt = 10 + Random.random(5);
        if (user.has(Trait.experienced)) {
            mt = mt * 2 / 3;
        }
        mt = Math.max(1, mt);
        results[0] = m;
        results[1] = mt;

        return results;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal || modifier == Result.upgrade) {
            return "You pound " + target.getName()
                            + " in the ass. She whimpers in pleasure and can barely summon the strength to hold herself off the floor.";
        } else if (modifier == Result.reverse) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:bounce|bounces} on {other:name-possessive} cock, relentlessly driving you both towards orgasm.",
                            user, target);
        } else {
            return "You rapidly pound your dick into " + target.getName()
                            + "'s pussy. Her pleasure-filled cries are proof that you're having an effect, but you're feeling it "
                            + "as much as she is.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s relentlessly pegs %s in the ass as %s %s and try to endure the sensation.",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("groan"));
        } else if (modifier == Result.upgrade) {
            return String.format("%s pistons into %s while pushing %s shoulders on the ground; %s tits"
                            + " are shaking above %s head while %s strapon stimulates %s prostate.",
                            user.subject(), target.nameDirectObject(), target.possessiveAdjective(),
                            user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective(), user.possessiveAdjective());
        } else if (modifier == Result.reverse) {
            return String.format("%s bounces on %s cock, relentlessly driving %s both toward orgasm.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            c.bothDirectObject(target));
        } else {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:rapidly pound|rapidly pounds} {self:possessive} {self:body-part:cock} into {other:possessive} {other:body-part:pussy}, "
                                            + "relentlessly driving %s toward orgasm.",
                            user, target, c.bothDirectObject(target));
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fuck opponent without holding back. Very effective, but dangerous";
    }

    @Override
    public String getName(Combat c, Character user) {
        if (c.getStance().penetratedBy(c, c.getStance().getPartner(c, user), user)) {
            return "Piston";
        } else {
            return "Bounce";
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
