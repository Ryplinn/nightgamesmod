package nightgames.pet.arms.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.pet.arms.Arm;

public class HealRay extends ArmSkill {

    public HealRay() {
        super("Heal Ray", 30);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean stamina = Random.random(2) == 0;
        boolean mojo = Random.random(2) == 0;

        String msg = "The %s at the end of {self:name-possessive} %s" + " starts glowing with a lime green light, ";

        if (stamina && mojo) {
            msg += "and {self:pronoun} perks up. {self:PRONOUN-ACTION:seem|seems} less tired and much more"
                            + " confident than before.";
            owner.heal(c, 10 + Random.random(20), "Heal Ray");
            owner.buildMojo(c, 5 + Random.random(15), "Heal Ray");
        } else if (stamina) {
            msg += "and it seems to eliminate some of {self:possessive} weariness.";
            owner.heal(c, 10 + Random.random(20), "Heal Ray");
        } else if (mojo) {
            msg += "and something changes in {self:direct-object}. {self:POSSESSIVE} movements"
                            + " seem more confident, and {self:pronoun-action:watch|watches} "
                            + "{other:name-do} with a hint of a smile.";
            owner.buildMojo(c, 5 + Random.random(15), "Heal Ray");
        } else {
            msg += "but it soon sputters and dies out. {self:PRONOUN-ACTION:are not|does not seem} pleased.";
        }

        c.write(GUIColor.limbColor(owner), Formatter.format(msg, owner, target, arm.getType()
                                                                         .getDesc(),
                        arm.getName()));

        return true;
    }

}
