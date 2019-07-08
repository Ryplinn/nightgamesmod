package nightgames.characters.body.arms;

import nightgames.characters.Character;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.Grab;
import nightgames.combat.Combat;

import java.util.Arrays;
import java.util.List;

public class Grabber extends RoboArm {

    Grabber(ArmManager manager) {
        super(manager, ArmType.GRABBER);
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Arrays.asList(new Grab());
    }

}
