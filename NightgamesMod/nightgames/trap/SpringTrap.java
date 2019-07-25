package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTrait;
import nightgames.stance.StandingOver;
import nightgames.status.Flatfooted;
import nightgames.status.Winded;

public class SpringTrap extends Trap {
    

    SpringTrap() {
        this(null);
    }
    
    private SpringTrap(CharacterType owner) {
        super("Spring Trap", owner);
    }

    @Override
    public void trigger(Character target) {
        if (!target.checkVsDc(Attribute.perception, 24 - target.getAttribute(Attribute.perception) + target.baseDisarm())) {
            if (target.human()) {
                GUI.gui.message(
                                "As you're walking, your foot hits something and there's a sudden debilitating pain in your groin. Someone has set up a spring-loaded rope designed "
                                                + "to shoot up into your nuts, which is what just happened. You collapse into the fetal position and pray that there's no one nearby.");
            } else if (target.location().humanPresent()) {
                GUI.gui.message("You hear a sudden yelp as your trap catches " + target.getName()
                                + " right in the cooch. She eventually manages to extract the rope from between her legs "
                                + "and collapses to the floor in pain.");
            }
            int m = 50 + target.getLevel() * 5;
            if (target.has(ClothingTrait.armored)) {
                m /= 2;
                target.pain(null, null, m);
            } else {
                if (target.has(Trait.achilles)) {
                    m += 20;
                }
                target.pain(null, null, m);
                target.addNonCombat(new Winded(target.getType()));
            }
            target.location().opportunity(target, this);
        } else if (target.human()) {
            GUI.gui.message(
                            "You spot a suspicious mechanism on the floor and prod it from a safe distance. A spring loaded line shoots up to groin height, which would have been "
                                            + "very unpleasant if you had kept walking.");
            target.location().remove(this);
        }
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Spring) && owner.has(Item.Rope);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner.getType();
        owner.consume(Item.Rope, 1);
        owner.consume(Item.Spring, 1);
        return "You manage to rig up a makeshift booby trap, which should prove quite unpleasant to any who stumbles upon it.";
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.getAttribute(Attribute.cunning) >= 10;
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 1));
        enc.engage(new Combat(attacker, victim, attacker.location(), new StandingOver(attacker.getType(), victim.getType())));
        attacker.location().remove(this);
    }

}
