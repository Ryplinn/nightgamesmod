package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.TentacleInjectAphrodisiac;
import nightgames.characters.body.arms.skills.TentacleInjectSensitivity;
import nightgames.characters.body.arms.skills.TentacleInjectVenom;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class TentacleInjector extends TentacleArm {
    public TentacleInjector(ArmManager manager) {
        super(manager, ArmType.TENTACLE_INJECTOR);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new TentacleInjectAphrodisiac(), new TentacleInjectSensitivity(), new TentacleInjectVenom());
    }

}
