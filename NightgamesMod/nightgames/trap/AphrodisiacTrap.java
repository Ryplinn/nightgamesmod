package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.Flatfooted;
import nightgames.status.Horny;

public class AphrodisiacTrap extends Trap {

    AphrodisiacTrap() {
        this(null);
    }
    
    private AphrodisiacTrap(CharacterType owner) {
        super("Aphrodisiac Trap", owner);
    }

    public void setStrength(Character user) {
        setStrength(user.getAttribute(Attribute.cunning) + user.getAttribute(Attribute.science) + user.getLevel() / 2);
    }

    @Override
    public void trigger(Character target) {
        if (!target.checkVsDc(Attribute.perception, 20 + target.baseDisarm())) {
            if (target.human()) {
                GUI.gui.message(
                                "You spot a liquid spray trap in time to avoid setting it off. You carefully manage to disarm the trap and pocket the potion.");
                target.gain(Item.Aphrodisiac);
                target.location().remove(this);
            }
        } else {
            if (target.human()) {
                GUI.gui.message(
                                "There's a sudden spray of gas in your face and the room seems to get much hotter. Your dick goes rock-hard and you realize you've been "
                                                + "hit with an aphrodisiac.");
            } else if (target.location().humanPresent()) {
                GUI.gui.message(
                                target.getName() + " is caught in your trap and sprayed with aphrodisiac. She flushes bright red and presses a hand against her crotch. It seems like "
                                                + "she'll start masturbating even if you don't do anything.");
            }
            target.addNonCombat(new Horny(target.getType(), (30 + getStrength()) / 10, 10, "Aphrodisiac Trap"));
            target.location().opportunity(target, this);
        }
    }
    
    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Aphrodisiac) && owner.has(Item.Tripwire) && owner.has(Item.Sprayer)
                        && !owner.has(Trait.direct);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner.getType();
        owner.consume(Item.Tripwire, 1);
        owner.consume(Item.Aphrodisiac, 1);
        owner.consume(Item.Sprayer, 1);
        return "You set up a spray trap to coat an unwary opponent in powerful aphrodisiac.";
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.getAttribute(Attribute.cunning) >= 12 && !owner.has(Trait.direct);
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 1));
        enc.engage(new Combat(attacker, victim, attacker.location()));
        attacker.location().remove(this);
    }
}
