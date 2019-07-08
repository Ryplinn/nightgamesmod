package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.DefabRay;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;

public class DefabCannon extends RoboArm {

    public DefabCannon(ArmManager manager) {
        super(manager, ArmType.DEFAB_CANNON);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Collections.singletonList(new DefabRay());
    }

}
