package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.StabilizerIdle;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class Stabilizer extends RoboArm {
    Stabilizer(ArmManager manager) {
        super(manager, ArmType.STABILIZER);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new StabilizerIdle());
    }
}
