package nightgames.characters.body.arms.skills;

import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;

public abstract class TentacleArmSkill extends ArmSkill {
    TentacleArmSkill(String name, int level) {
        super(name, level);
    }

    // don't use arm skills while engulfed
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && c.getStance().distance() > 0;
    }

    public abstract boolean resolve(Combat c, Arm arm, Character owner, Character target);
}
