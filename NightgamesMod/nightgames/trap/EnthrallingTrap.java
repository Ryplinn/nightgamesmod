package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.Enthralled;
import nightgames.status.Flatfooted;

public class EnthrallingTrap extends Trap {

    public EnthrallingTrap() {
        this(null);
    }

    public void setStrength(Character user) {
        setStrength(user.getAttribute(Attribute.darkness) + user.getAttribute(Attribute.spellcasting) + user.getLevel() / 2);
    }

    public EnthrallingTrap(CharacterType owner) {
        super("Enthralling Trap", owner);
    }

    @Override
    public void trigger(Character target) {
        if (target.human()) {
            if (target.checkVsDc(Attribute.perception, 25 + target.baseDisarm())
                            || !target.eligible(getOwner()) || !getOwner().eligible(target)) {
                GUI.gui.message("As you step across the " + target.location().name
                                + ", you notice a pentagram drawn on the floor,"
                                + " appearing to have been drawn in cum. Wisely," + " you avoid stepping into it.");
            } else {
                target.location().opportunity(target, this);
                GUI.gui.message("As you step across the " + target.location().name
                                + ", you are suddenly surrounded by purple flames. Your mind "
                                + "goes blank for a moment, leaving you staring into the distance."
                                + " When you come back to your senses, you shake your head a few"
                                + " times and hope whatever that thing was, it failed at"
                                + " whatever it was supposed to do. The lingering vision of two"
                                + " large red irises staring at you suggest differently, though.");
                target.addNonCombat(new Enthralled(target.getType(), owner, 5 + getStrength() / 20));
            }
        } else if (target.checkVsDc(Attribute.perception, 25 + target.baseDisarm()) || !target.eligible(getOwner()) || !getOwner().eligible(target)) {
            if (target.location().humanPresent()) {
                GUI.gui.message("You catch a bout of purple fire in your peripheral vision,"
                                + "but once you have turned to look the flames are gone. All that is left"
                                + " to see is " + target.getName() + ", standing still and staring blankly ahead."
                                + " It would seem to be very easy to have your way with her now, but"
                                + " who or whatever left that thing there will probably be thinking" + " the same.");
            }
            target.addNonCombat(new Enthralled(target.getType(), owner, 5 + getStrength() / 20));
            target.location().opportunity(target, this);
        }
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.semen);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.getAttribute(Attribute.darkness) > 5;
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner.getType();
        owner.consume(Item.semen, 1);
        return "You pop open a bottle of cum and use its contents to draw"
                        + " a pentagram on the floor, all the while speaking"
                        + " incantations to cause the first person to step into"
                        + " it to be immediately enthralled by you.";
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 1));
        enc.engage(new Combat(attacker, victim, attacker.location()));
        attacker.location().remove(this);
    }

}
