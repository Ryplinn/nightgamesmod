package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.arms.Arm;
import nightgames.characters.body.arms.TentacleSucker;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.status.PartSucked;

public class TentacleSuck extends TentacleArmSkill {    
    public TentacleSuck() {
        super("Tentacle Suck", 20);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && target.hasDick() && c.getStance().distance() < 2 && !c.getStance().penisInserted(target);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = target.bound() || !c.getStance().mobile(target);
        boolean success = sub || Random.random(100) < 10 + owner.getAttribute(Attribute.slime);
        double strength = Random.random(10, 21);
        BodyPart tentaclePart = TentacleSucker.PART;

        if (success) {
            c.write(GUIColor.limbColor(owner), Formatter.format("{self:NAME-POSSESSIVE} %s shoots forward, snaking through {other:possessive} guard "
                            + "and attaching itself to {other:possessive} defenseless cock. "
                            + "{other:SUBJECT-ACTION:try} pulling it off {other:reflective} with {other:possessive} hands but the vacuum-tight "
                            + "suction make it feel like {other:pronoun-action:are} giving {other:reflective} a tug-job.", owner, target, arm.getName()));
            target.body.pleasure(owner, tentaclePart, target.body.getRandomPussy(), strength, c);
            target.add(c, new PartSucked(target.getType(), owner.getType(), tentaclePart, "cock"));
            return true;
        } else {
            c.write(GUIColor.limbColor(owner), Formatter.format("A %s flies towards {other:name-possessive} crotch, "
                            + "but {other:pronoun-action:dodge} out of the way just in time.", owner, target, arm.getName()));
        }
        return false;
    }
}
