package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.TentacleSquirt;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;

public class TentacleSquirter extends TentacleArm {
    public TentacleSquirter(ArmManager manager) {
        super(manager, ArmType.TENTACLE_SQUIRTER);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Collections.singletonList(new TentacleSquirt());
    }
}
