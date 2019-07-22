package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.skills.ThrowSlime;
import nightgames.skills.ThrowSlime.HitType;
import nightgames.status.Slimed;

public class TentacleSquirt extends TentacleArmSkill {    
    public TentacleSquirt() {
        super("Tentacle Squirt", 10);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && c.getStance().distance() > 1;
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = target.bound() || !c.getStance().mobile(target);
        boolean success = sub || Random.random(100) < 10 + owner.get(Attribute.slime);
        ThrowSlime throwSlimeSkill = new ThrowSlime(owner.getType(), owner.get(Attribute.slime));
        HitType type = throwSlimeSkill.decideEffect(c, target);

        if (success && type != HitType.NONE) {
            c.write(GUIColor.limbColor(owner), Formatter
                            .format("The %s rears up and fires a large gunk of slime at {other:name-do}", owner, target, arm.getName()));
            type.message(c, owner, target);
            target.add(c, type.build(owner, target));
            target.add(c, new Slimed(target.getType(), owner.getType(), Random.random(1, 5)));
            return true;
        } else {
            c.write(GUIColor.limbColor(owner), Formatter.format("The %s rears up and fires a large gunk of slime at {other:name-do}. Luckily, it misses its mark.", owner, target, arm.getName()));
        }
        return false;
    }
}
