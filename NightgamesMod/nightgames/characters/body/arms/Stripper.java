package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.Strip;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class Stripper extends RoboArm {
    Stripper(ArmManager manager) {
        super(manager, ArmType.STRIPPER);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new Strip());
    }

}
