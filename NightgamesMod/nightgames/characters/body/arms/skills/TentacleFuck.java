package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.arms.Arm;
import nightgames.characters.body.arms.TentacleArm;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.status.PartFucked;

public class TentacleFuck extends TentacleArmSkill {    
    public TentacleFuck() {
        super("Tentacle Fuck", 20);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && target.hasPussy() && c.getStance().distance() < 2 && !c.getStance().vaginallyPenetrated(c, target);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = target.bound() || !c.getStance().mobile(target);
        boolean success = sub || Random.random(100) < 10 + owner.getAttribute(Attribute.slime);
        double strength = Random.random(10, 21);
        
        BodyPart tentaclePart;
        if (arm instanceof TentacleArm) {
            tentaclePart = ((TentacleArm)arm).getPart();
        } else {
            tentaclePart = TentacleArm.PART;
        }

        if (success) {
            c.write(GUIColor.limbColor(owner), Formatter.format("{self:NAME-POSSESSIVE} %s shoots forward, snaking through {other:possessive} guard "
                            + "and impaling itself inside {other:possessive} defenseless pussy. "
                            + "{other:SUBJECT:try} pulling it out with {other:possessive} hands but the slippery appendage easily eludes {other:possessive} grip. "
                            + "The entire business just ends ups arousing {other:direct-object} to no end.", owner, target, arm.getName()));
            target.body.pleasure(owner, tentaclePart, target.body.getRandomPussy(), strength, c);
            target.add(c, new PartFucked(target.getType(), owner.getType(), tentaclePart, "pussy"));
            return true;
        } else {
            c.write(GUIColor.limbColor(owner), Formatter.format("A %s flies towards {other:name-possessive} crotch, "
                            + "but {other:pronoun-action:dodge} out of the way just in time.", owner, target, arm.getName()));
        }
        return false;
    }

}
