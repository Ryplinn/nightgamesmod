package nightgames.characters.body.arms.skills;

import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;

public class Idle extends ArmSkill {
    public Idle() {
        super("Idle", 0);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_PET)) {
            System.out.println(arm.getName() + " idling");
        }
        return true;
    }
}
