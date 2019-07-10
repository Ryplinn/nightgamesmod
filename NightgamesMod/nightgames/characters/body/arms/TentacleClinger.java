package nightgames.characters.body.arms;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.TentacleCling;
import nightgames.characters.body.arms.skills.TentacleReel;
import nightgames.combat.Combat;
import nightgames.status.Stsflag;

import java.util.Arrays;
import java.util.List;

public class TentacleClinger extends TentacleArm {

    public TentacleClinger(ArmManager manager) {
        super(manager, ArmType.TENTACLE_CLINGER);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new TentacleCling(), new TentacleReel());
    }

    @Override
    int attackOdds(Combat c, Character owner, Character target) {
        if (target.is(Stsflag.tentacleBound)) {
            return 100;
        }
        return (int) Math.min(40, 5 + owner.get(Attribute.slime) * .4);
    }
}
