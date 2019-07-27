package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Lethargic;

public class SpiralThrust extends Thrust {
    private int cost;

    public SpiralThrust() {
        super("Spiral Thrust");
        cost = 0;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.spiral);
    }
    
    @Override
    public float priorityMod(Combat c, Character user) {
        // Prefer 80+% mojo, or it would be a waste
        return (100 - user.getMojo().percent() - 80) / 10;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        cost = Math.max(20, user.getMojo().get());
        return cost;
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] result = new int[2];
        int x = cost;
        int mt = x / 2;
        if (user.has(Trait.experienced)) {
            mt = mt * 2 / 3;
        }
        result[0] = x;
        result[1] = mt;

        return result;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean res = super.resolve(c, user, target, rollSucceeded);
        if (res) {
            user.add(c, new Lethargic(user.getType(), 30, .75));
        }
        return res;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return "You unleash your strongest technique into " + target.getName()
                            + "'s ass, spiraling your hips and stretching her tight sphincter.";
        } else if (modifier == Result.reverse) {
            return Formatter.format("As you bounce on " + target.getName()
                            + "'s steaming pole, you feel a power welling up inside you. You put everything you have into moving your hips circularly, "
                            + "rubbing every inch of her cock with your hot slippery "
                            + getSelfOrgan(c, user, target).fullDescribe(user) + ".", user, target);
        } else {
            return "As you thrust into " + target.getName()
                            + "'s hot pussy, you feel a power welling up inside you. You put everything you have into moving your hips circularly "
                            + "while you continue to drill into her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(c, user, target);
        if (modifier == Result.anal) {
            return String.format("%s drills into %s ass with extraordinary power. %s head seems to go"
                            + " blank and %s %s face down to the ground as %s arms turn to jelly and give out.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            Formatter.capitalizeFirstLetter(target.nameOrPossessivePronoun()),
                            target.pronoun(), target.action("fall"), target.possessiveAdjective());
        } else if (modifier != Result.reverse) {
            return Formatter.format(
                            "The movements of {self:name-possessive} cock suddenly change. {self:PRONOUN} suddenly begins "
                            + "drilling {other:name-possessive} poor pussy with an unprecedented passion. "
                                            + "The only thing {other:subject} can do is bite {other:possessive} lips and try to not instantly cum.",
                            user, target);
        } else {
            return String.format("%s begins to move %s hips wildly in circles, rubbing every inch"
                            + " of %s cock with %s hot, %s, bringing %s more pleasure than %s thought possible.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            (selfO.isType("pussy") ? "slippery pussy walls" : " steaming asshole"),
                            target.directObject(), target.pronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Converts your mojo into fucking: All Mojo";
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (c.getStance().penetratedBy(c, c.getStance().getPartner(c, user), user)) {
            return "Spiral Thrust";
        } else {
            return "Spiral";
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
