package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.ToysPart;
import nightgames.characters.body.arms.Arm;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.items.clothing.ClothingSlot;
import nightgames.skills.damage.DamageType;

public class ToyAttack extends ArmSkill {

    public ToyAttack() {
        super("Toy Attack", 20);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && target.outfit.slotOpen(ClothingSlot.bottom);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {

        int m = 5 + Random.random(10);
        m += (int) DamageType.gadgets.modifyDamage(owner, target, 2);
        if (c.getStance()
             .penetrated(c, target) && target.hasDick()) {
            c.write(GUIColor.limbColor(owner),
                            Formatter.format("It would seem {self:name-possessive} %s"
                                            + " is taking pity on {other:name-possessive} {other:body-part:cock},"
                                            + " neglected as it currently is. The malleable material at its tip forms"
                                            + " a tight, elastic hole, which gobbles up the sensitive rod. It gives some"
                                            + " powerful sucks and wriggles before mercifully disengaging.", owner,
                                            target, arm.getName()));

            target.body.pleasure(owner, ToysPart.onahole, target.body.getRandomCock(), m, c);
        } else if (c.getStance()
                    .penetrated(c, owner)) {
            boolean anal = !target.body.has("pussy");
            String cock = target.has(Trait.strapped) ? "strapon" : "{other:body-part:cock}";
            String hole = anal ? "{other:body-part:ass}" : "{other:body-part:pussy}";
            c.write(GUIColor.limbColor(owner),
                            Formatter.format("{other:NAME-POSSESSIVE} %s may be occupied right now,"
                                            + " but that won't stop {self:name-possessive} %s from going after {other:possessive}"
                                            + " %s! The soft material at its tip solidifies into a hard, intimidatingly long"
                                            + " dildo, and it wastes little time in going after the poor hole. The pain only lasts"
                                            + " briefly before all the thrusting leaves {other:direct-object} gasping with pleasure.",
                                            owner, target, cock, arm.getName(), hole));
            target.body.pleasure(owner, ToysPart.dildo, target.body.getRandomHole(), m, c);
        } else {
            String part;
            boolean pussyAvailable = target.body.has("pussy") && !c.getStance().vaginallyPenetrated(c, target);
            boolean cockAvailable = target.body.has("cock") && !c.getStance().penetratedBy(c, owner, target);
            
            if (pussyAvailable && cockAvailable) {
                part = Random.random(3) == 0 ? "pussy" : Random.random(2) == 0 ? "cock" : "ass";
            } else if (cockAvailable) {
                part = Random.random(2) == 0 ? "cock" : "ass";
            } else if (pussyAvailable) {
                part = Random.random(2) == 0 ? "pussy" : "ass";
            } else {
                part = "ass";
            }
            if (c.getStance()
                 .dom(owner) || Random.random(100) < owner.getAttribute(Attribute.science) + owner.getAttribute(Attribute.cunning)) {
                if (part.equals("pussy")) {
                    c.write(GUIColor.limbColor(owner), Formatter.format("{self:NAME-POSSESSIVE} %s shapes itself"
                                    + " into a cock-like column of material and shoots straight at"
                                    + " {other:name-possessive} unprotected {other:body-part:pussy}."
                                    + " The slippery dildo has no trouble pushing past {other:possessive}"
                                    + " soft folds, and pumps rhythmically in and out of the soon-sopping hole."
                                    , owner, target, arm.getName()));
                    target.body.pleasure(owner, ToysPart.dildo, target.body.getRandomPussy(), m, c);
                } else if (part.equals("cock")) {
                    c.write(GUIColor.limbColor(owner), Formatter.format("The particles at the tip of {self:NAME-POSSESSIVE}"
                                    + " %s arrange themselves into a cup-like shape. That cup soon settles"
                                    + " around {other:name-possessive} {other:body-part:cock}, and it"
                                    + " massages it with vigor.", owner, target, arm.getName()));
                    target.body.pleasure(owner, ToysPart.onahole, target.body.getRandomCock(), m, c);
                } else {
                    c.write(GUIColor.limbColor(owner), Formatter.format("{self:NAME-POSSESSIVE} %s forms a dildo at its"
                                    + " end and sneaks up behind {other:name-do}. Before "
                                    + "{other:pronoun-action:have|has} a chance to react, the toy worms its"
                                    + " way past {other:possessive} asscheeks and plunges into {other:possessive}"
                                    + " ass."
                                    , owner, target, arm.getName()));
                    target.body.pleasure(owner, ToysPart.dildo, target.body.getRandomAss(), m, c);
                }
            } else {
                c.write(GUIColor.limbColor(owner), Formatter.format("The amorphous head of {self:name-possessive}"
                                + " %s wiggles and churns, but fails to take on any recognizable shape."
                                , owner, target, arm.getName()));
            }
        }

        return true;
    }


}
