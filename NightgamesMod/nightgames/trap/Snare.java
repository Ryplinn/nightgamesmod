package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.IEncounter;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.status.Bound;

public class Snare extends Trap {
    public Snare() {
        this(null);
    }

    public Snare(Character owner) {
        super("Snare", owner);
    }

    public void setStrength(Character user) {
        setStrength(user.get(Attribute.Cunning) + user.getLevel() / 2);
    }

    @Override
    public void trigger(Character target) {
        if (target.check(Attribute.Perception, 25 + getStrength() + target.baseDisarm())) {
            if (target.human()) {
                Global.gui().message("You notice a snare on the floor in front of you and manage to disarm it safely");
            }
            target.location().remove(this);
        } else {
            target.addNonCombat(new Bound(target, 30 + getStrength() / 2, "snare"));
            if (target.human()) {
                Global.gui().message(
                                "You hear a sudden snap and you're suddenly overwhelmed by a blur of ropes. The tangle of ropes trip you up and firmly bind your arms.");
            } else if (target.location().humanPresent()) {
                Global.gui().message(target.name()
                                + " enters the room, sets off your snare, and ends up thoroughly tangled in rope.");
            }
            target.location().opportunity(target, this);
        }
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Tripwire) && owner.has(Item.Rope);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.consume(Item.Tripwire, 1);
        owner.consume(Item.Rope, 1);
        return "You carefully rig up a complex and delicate system of ropes on a tripwire. In theory, it should be able to bind whoever triggers it.";
    }
    
    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 9;
    }

    @Override
    public void capitalize(Character attacker, Character victim, IEncounter enc) {
        enc.engage(new Combat(attacker, victim, attacker.location()));
        attacker.location().remove(this);
    }
}
