package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTable;
import nightgames.items.clothing.ClothingTrait;

public class NursesGloves extends Skill {
    private NursesGloves() {
        super("Nurse's Gloves", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.medicine) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !user.has(ClothingTrait.nursegloves)
                        && user.has(Item.MedicalSupplies, 1);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Puts on a pair of plastic medical examiner's gloves";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        user.getOutfit().equip(ClothingTable.getByID("nursesgloves"));
        user.consume(Item.MedicalSupplies, 1);

        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = "You grab a pair of rubber gloves, pulling them on with a satisfying snap.";
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = String.format("With a lecherous grin on %s face, %s snaps on a pair of"
                        + " rubber gloves similar to those you would see at the doctor's.",
                        user.possessiveAdjective(), user.subject());
        return message;
    }

}
