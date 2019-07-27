package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTable;
import nightgames.nskills.tags.SkillTag;

public class LivingClothingOther extends Skill {
    LivingClothingOther() {
        super("Living Clothing: Other", 8);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance().mobile(target) && c.getStance().mobile(user)
                        && !c.getStance().inserted() && target.torsoNude() && user.has(Item.Battery, 3);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fabricate a living suit of tentacles to wrap around your opponent: 3 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.consume(Item.Battery, 3);
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        ClothingTable.getByID("tentacletop").ifPresent(top -> target.getOutfit().equip(top));
        ClothingTable.getByID("tentaclebottom").ifPresent(bottom -> target.getOutfit().equip(bottom));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = "You power up your fabricator and dial the knob to the emergency reclothing setting. "
                        + "You hit the button and dark tentacles squirm out of the device. " + "You hold "
                        + target.subject() + " down and point the tentacles at her body. "
                        + "The undulating tentacles coils around " + target.possessiveAdjective()
                        + " body and wraps itself into a living suit.";
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = String.format("While holding %s down, %s powers up %s fabricator and dials the knob"
                        + " to the emergency reclothing setting. %s hits the button and dark tentacles squirm"
                        + " out of the device. The created tentacles coils around %s body and"
                        + " wrap themselves into a living suit.", target.nameDirectObject(),
                        user.subject(), user.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(user.pronoun()),
                        target.nameOrPossessivePronoun());
        return message;
    }

}
