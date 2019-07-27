package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;
import nightgames.status.Stsflag;

import java.util.List;

public class Strapon extends Skill {

    public Strapon() {
        super("Strap On", 15);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !user.has(Trait.strapped) && c.getStance().mobile(user)
                        && !c.getStance().prone(user)
                        && (user.has(Item.Strapon) || user.has(Item.Strapon2)) && !user.hasDick()
                        && !c.getStance().connected(c) && !c.getStance().isFaceSitting(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Put on the strapon dildo";
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        List<Clothing> unequipped = user.getOutfit().equip(ClothingTable.getByID("strapon"));
        if (unequipped.isEmpty()) {
            if (user.human()) {
                c.write(user, Formatter.capitalizeFirstLetter(deal(c, 0, Result.normal, user, target)));
            } else if (!target.is(Stsflag.blinded)) {
                c.write(user, Formatter.capitalizeFirstLetter(receive(c, 0, Result.normal, user, target)));
            } else {
                printBlinded(c, user);
            }
        } else {
            if (user.human()) {
                c.write(user, "You take off your " + unequipped.get(0)
                                + " and fasten a strap on dildo onto yourself.");
            } else if (!target.is(Stsflag.blinded)){
                c.write(user,
                                String.format("%s takes off %s %s and straps on a thick rubber "
                                                + "cock and grins at %s in a way that makes %s feel a bit nervous.",
                                                user.subject(), user.possessiveAdjective(),
                                                unequipped.get(0), target.nameDirectObject(),
                                                target.directObject()));
            } else printBlinded(c, user);
        }
        if (!target.is(Stsflag.blinded)) {
            target.loseMojo(c, 10);
            target.emote(Emotion.nervous, 10);
        }
        user.emote(Emotion.confident, 30);
        user.emote(Emotion.dominant, 40);
        Item lost = user.has(Item.Strapon2) ? Item.Strapon2 : Item.Strapon;
        c.getCombatantData(user).loseItem(lost);
        user.remove(lost);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You put on a strap on dildo.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s straps on a thick rubber cock and grins in a way that "
                        + "makes %s feel a bit nervous.", user.subject(),
                        target.nameDirectObject());
    }

}
