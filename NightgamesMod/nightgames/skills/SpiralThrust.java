package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.status.Lethargic;

public class SpiralThrust extends Thrust {
    int cost;

    public SpiralThrust(Character self) {
        super("Spiral Thrust", self);
        cost = 0;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.spiral);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().canthrust(getSelf()) && c.getStance().inserted()
                        && c.getStance().havingSexOtherNoStrapped(getSelf());
    }
    
    @Override
    public float priorityMod(Combat c) {
        // Prefer 80+% mojo, or it would be a waste
        return (100 - getSelf().getMojo().percent() - 80) / 10;
    }

    @Override
    public int getMojoCost(Combat c) {
        cost = Math.max(20, getSelf().getMojo().get());
        return cost;
    }

    @Override
    public int[] getDamage(Combat c, Character target) {
        int[] result = new int[2];
        int x = cost;
        int mt = x / 2;
        if (getSelf().has(Trait.experienced)) {
            mt = mt * 2 / 3;
        }
        result[0] = x;
        result[1] = mt;

        return result;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 0;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        boolean res = super.resolve(c, target);
        if (res) {
            getSelf().add(c, new Lethargic(getSelf(), 30, .75));
        }
        return res;
    }

    @Override
    public Skill copy(Character user) {
        return new SpiralThrust(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.anal) {
            return "You unleash your strongest technique into " + target.name()
                            + "'s ass, spiraling your hips and stretching her tight sphincter.";
        } else if (modifier == Result.reverse) {
            return Global.format("As you bounce on " + target.name()
                            + "'s steaming pole, you feel a power welling up inside you. You put everything you have into moving your hips circularly, "
                            + "rubbing every inch of her cock with your hot slippery "
                            + getSelfOrgan(c).fullDescribe(getSelf()) + ".", getSelf(), target);
        } else {
            return "As you thrust into " + target.name()
                            + "'s hot pussy, you feel a power welling up inside you. You put everything you have into moving your hips circularly "
                            + "while you continue to drill into her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        BodyPart selfO = getSelfOrgan(c);
        if (modifier == Result.anal) {
            return getSelf().name()
                            + " drills into your ass with extraordinary power. Your head seems to go blank and you fall face down to the ground as your arms turn to jelly and give out.";
        } else if (modifier != Result.reverse) {
            return Global.format(
                            "The movements of {self:name-possessive} cock suddenly change. She suddenly begins drilling to your poor pussy with an unprecedented passion. "
                                            + "The only thing you can do is bite your lips and try to not instantly cum.",
                            getSelf(), target);
        } else {
            return getSelf().name()
                            + " begins to move her hips wildly in circles, rubbing every inch of your cock with her hot, "
                            + (selfO.isType("pussy") ? "slippery pussy walls" : " steaming asshole")
                            + ", bringing you more pleasure " + "than you thought possible.";
        }
    }

    @Override
    public String describe(Combat c) {
        return "Converts your mojo into fucking: All Mojo";
    }

    @Override
    public String getLabel(Combat c) {
        if (c.getStance().inserted(getSelf())) {
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
