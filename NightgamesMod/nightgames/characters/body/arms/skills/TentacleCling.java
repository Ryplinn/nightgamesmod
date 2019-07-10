package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.status.Stsflag;
import nightgames.status.TentacleBound;

public class TentacleCling extends TentacleArmSkill {
    public TentacleCling() {
        super("Cling", 20);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && !target.is(Stsflag.tentacleBound);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = c.getStance().dom(owner);
        int chance = Math.min(50, 15 + owner.get(Attribute.slime));
        if (sub) {
            chance += 30;
        }
        boolean success = Random.random(100) < chance;

        if (success) {
            c.write(GUIColor.limbColor(owner), Formatter.format("A %s shoots out from behind {self:name-do}"
                            + " and wraps itself around {other:name-possessive} waist, restricting {other:possessive} movement.", owner, target, arm.getName()));
            target.add(c, new TentacleBound(target, 30 + 4 * Math.sqrt(owner.get(Attribute.slime)), owner.nameOrPossessivePronoun() + " " + arm.getName(), 1));
        } else {
            c.write(GUIColor.limbColor(owner), Formatter.format("A %s shoots out from behind {self:name-do}"
                            + " and attempts to wrap itself around {other:name-possessive} waist. "
                            + "However, {other:pronoun-action:manage} to twist away just in time.", owner, target, arm.getName()));
        }
        return false;
    }

}
