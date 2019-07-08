package nightgames.pet.arms.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.gui.GUIColor;
import nightgames.pet.arms.Arm;
import nightgames.stance.Stance;

public class StabilizerIdle extends ArmSkill {

    public StabilizerIdle() {
        super("Stabilizer Idle", 20);
    }
    
    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && c.getStance().en == Stance.neutral;
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        if (c.getStance().en == Stance.neutral) {
            c.write(GUIColor.limbColor(owner), Formatter.format("The stabilizer is idling behind {self:subject},"
                            + " low to the ground and ready to break {self:possessive} fall should"
                            + " {other:subject} get any ideas.", owner, target));
        }
        return true;
    }

}
