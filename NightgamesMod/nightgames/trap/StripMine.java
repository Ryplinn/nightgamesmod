package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.Flatfooted;

import java.util.stream.IntStream;

public class StripMine extends Trap {
    
    StripMine() {
        this(null);
    }
    
    private StripMine(CharacterType owner) {
        super("Strip Mine", owner);
    }

    @Override
    public void trigger(Character target) {
        if (target.human()) {
            if (target.mostlyNude()) {
                GUI.gui.message(
                                "You're momentarily blinded by a bright flash of light. A camera flash maybe? Is someone taking naked pictures of you?");
            } else {
                GUI.gui.message(
                                "You're suddenly dazzled by a bright flash of light. As you recover from your disorientation, you notice that it feel a bit drafty. "
                                                + "You find you're missing some clothes. You reflect that your clothing expenses have gone up significantly since you joined the Games.");
            }
        } else if (target.location().humanPresent()) {
            GUI.gui.message("You're startled by a flash of light not far away. Standing there is a half-naked "
                            + target.getName() + ", looking surprised.");
        }
        IntStream.range(0, 2 + Random.random(4)).forEach(i -> target.shredRandom());
        target.location().opportunity(target, this);
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Tripwire) && owner.has(Item.Battery, 3);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.getAttribute(Attribute.science) >= 4;
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner.getType();
        owner.consume(Item.Tripwire, 1);
        owner.consume(Item.Battery, 3);
        return "Using the techniques Jett showed you, you rig up a one-time-use clothing destruction device.";
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 1));
        enc.engage(new Combat(attacker, victim, attacker.location()));
        attacker.location().remove(this);
    }

}
