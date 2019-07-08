package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.ToyAttack;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class ToyArm extends RoboArm {
    ToyArm(ArmManager manager) {
        super(manager, ArmType.TOY_ARM);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new ToyAttack());
    }
}
