package nightgames.characters.body.arms;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;

public abstract class RoboArm extends Arm {
    RoboArm(ArmManager manager, ArmType type) {
        super(manager, type);
    }

    @Override
    public String describe() {
        return "A long, segmented metal arm with " + getType().getDesc() + " at its tip.";
    }

    @Override
    int attackOdds(Combat c, Character owner, Character target) {
        return (int) Math.min(40, owner.getAttribute(Attribute.science) * .67);
    }
}
