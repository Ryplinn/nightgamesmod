package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;

public class StealClothes extends Skill {

    StealClothes() {
        super("Steal Clothes");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return (c.getStance().reachTop(user) && !target.outfit
                        .slotEmptyOrMeetsCondition(ClothingSlot.top, clothing -> blocked(user, clothing))) || (
                        c.getStance().reachBottom(user) && !target.outfit.slotEmptyOrMeetsCondition(ClothingSlot.bottom,
                                        clothing -> blocked(user, clothing)));

    }

    private boolean blocked(Character user, Clothing c) {
        // Allow crossdressing for now, except for strapons and bras.
        // This may change.
        if ((user.hasDick() && c.getID()
                                     .equals("strapon"))
                        || (!user.hasBreasts() && c.getSlots()
                                                        .contains(ClothingSlot.top)
                                        && c.getLayer() == 0))
            return false;

        return user.outfit.canEquip(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Steal and put on an article of clothing: 10 Mojo.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Clothing stripped;
        boolean top;
        if (!target.outfit.slotEmptyOrMeetsCondition(ClothingSlot.top, c1 -> blocked(user, c1))) {
            stripped = target.outfit.unequip(target.outfit.getTopOfSlot(ClothingSlot.top));
            user.outfit.equip(stripped);
            top = true;
        } else if (!target.outfit.slotEmptyOrMeetsCondition(ClothingSlot.bottom, c1 -> blocked(user, c1))) {
            stripped = target.outfit.unequip(target.outfit.getTopOfSlot(ClothingSlot.bottom));
            user.outfit.equip(stripped);
            top = false;
        } else {
            c.write(user, "<b>Error: Couldn't strip anything in StealClothes#resolve</b>");
            return false;
        }
        c.write(user, String.format(
                        "%s %s with %s quick movements, and before %s %s what's" + " going on, %s %s graces %s %s.",
                        user.subjectAction("dazzle"), target.subject(), user.possessiveAdjective(),
                        target.pronoun(), target.action("realize"), target.possessiveAdjective(),
                        stripped.getName(), user.nameOrPossessivePronoun(), top ? "chest" : "hips"));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
