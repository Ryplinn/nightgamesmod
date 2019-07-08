package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.HeatRay;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class HeatCannon extends RoboArm {

    public HeatCannon(ArmManager manager) {
        super(manager, ArmType.HEAT_RAY);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new HeatRay());
    }

}
