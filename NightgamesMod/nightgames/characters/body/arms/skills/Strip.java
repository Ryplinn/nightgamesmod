package nightgames.characters.body.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Strip extends ArmSkill {

    public Strip() {
        super("Strip", 20);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && !target.outfit.isNude();
    }
    
    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = c.getStance().dom(owner);
        double accuracy = 10 + owner.get(Attribute.science);
        boolean hasTop = !target.outfit.slotEmpty(ClothingSlot.top);
        boolean hasBottom = !target.outfit.slotEmpty(ClothingSlot.bottom);
        ClothingSlot slot;
        
        if (hasTop && hasBottom) {
            slot = Random.random(2) == 0 ? ClothingSlot.top : ClothingSlot.bottom;
        } else if (hasTop) {
            slot = ClothingSlot.top;
        } else if (hasBottom) {
            slot = ClothingSlot.bottom;
        } else {
            accuracy *= 1.5;
            slot = Random.pickRandomGuaranteed(Arrays.stream(ClothingSlot.values()).filter(s -> !target.outfit.
                            slotEmpty(s)).collect(Collectors.toList()));
        }
        
        if (sub || Random.random(100) < accuracy) {
            Clothing stripped = target.outfit.getTopOfSlot(slot);
            if (stripped.is(ClothingTrait.indestructible)) {
                c.write(GUIColor.limbColor(owner), Formatter.format("A %s lunges at {other:name-do}, its attachment"
                                + " grabbing for {other:possessive} %s. It fails to remove it, however."
                                , owner, target, arm.getName(), stripped.getName()));
            } else {
                c.write(GUIColor.limbColor(owner), Formatter.format("A steely blur flies at {other:name-do},"
                                + " revealing itself to be a %s which has already latched on"
                                + " to {other:possessive} %s. The poor garment is no match"
                                + " for {self:name-possessive} technological advantage and it falls"
                                + " to the ground in tatters as the arm rips it off of {other:direct-object}."
                                , owner, target, arm.getName(), stripped.getName()));
                target.shred(slot);
                return true;
            }
        } else {
            c.write(GUIColor.limbColor(owner), Formatter.format("{other:SUBJECT-ACTION:manage|manages} to dodge"
                            + " {self:name-possessive} %s as it flies at {other:possessive} clothes.",
                            owner, target, arm.getName()));
        }
        
        return false;
    }

}
