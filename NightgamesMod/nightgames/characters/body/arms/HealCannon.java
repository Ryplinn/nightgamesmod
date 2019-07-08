package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.HealRay;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;

public class HealCannon extends RoboArm {

    public HealCannon(ArmManager manager) {
        super(manager, ArmType.HEAL_CANNON);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Collections.singletonList(new HealRay());
    }

}
