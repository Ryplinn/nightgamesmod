package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTable;
import nightgames.nskills.tags.SkillTag;

public class LivingClothing extends Skill {
    public LivingClothing() {
        super("Living Clothing: Self", 5);
        addTag(SkillTag.suicidal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.science) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().inserted()
                        && user.torsoNude() && user.has(Item.Battery, 3);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fabricate a living suit of tentacles to wear: 3 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Battery, 3);
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        user.getOutfit().equip(ClothingTable.getByID("tentacletop"));
        user.getOutfit().equip(ClothingTable.getByID("tentaclebottom"));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = "You power up your fabricator and dial the knob to the emergency reclothing setting. "
                        + "You hit the button and dark tentacles squirm out of the device. The tentacles coils around your body and wraps itself into a living suit.";
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = String.format("With a grimace, %s powers up %s fabricator and dials the "
                        + "knob to the emergency reclothing setting. %s hits the button and dark tentacles"
                        + " squirm out of the device. The created tentacles coils around %s body"
                        + " and wrap themselves into a living suit.", user.subject(),
                        user.possessiveAdjective(), Formatter.capitalizeFirstLetter(user.pronoun()),
                        user.nameOrPossessivePronoun());
        return message;
    }

}
