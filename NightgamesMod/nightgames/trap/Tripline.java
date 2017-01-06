package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.IEncounter;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.stance.StandingOver;
import nightgames.status.Flatfooted;

public class Tripline extends Trap {
    
    public Tripline() {
        this(null);
    }
    
    public Tripline(Character owner) {
        super("Tripline", owner);
    }

    @Override
    public void trigger(Character target) {
        int m = 30 + target.getLevel() * 5;
        if (target.human()) {
            if (!target.check(Attribute.Perception, 20 + target.baseDisarm())) {
                Global.gui().message("You trip over a line of cord and fall on your face.");
                target.pain(null, null, m);
                target.location().opportunity(target, this);
            } else {
                Global.gui().message("You spot a line strung across the corridor and carefully step over it.");
                target.location().remove(this);
            }
        } else {
            if (!target.check(Attribute.Perception, 20 + target.baseDisarm())) {
                if (target.location().humanPresent()) {
                    Global.gui().message(target.name()
                                    + " carelessly stumbles over the tripwire and lands with an audible thud.");
                }
                target.pain(null, null, m);
                target.location().opportunity(target, this);
            } else {
                if (target.location().humanPresent()) {
                    Global.gui().message("You see " + target.getName() + " carefully step over the carefully placed tripline." );
                }
            }
        }
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Rope);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.consume(Item.Rope, 1);
        return "You run a length of rope at ankle height. It should trip anyone who isn't paying much attention.";
    }

    @Override
    public boolean requirements(Character owner) {
        return true;
    }

    @Override
    public void capitalize(Character attacker, Character victim, IEncounter enc) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        enc.engage(new Combat(attacker, victim, attacker.location(), new StandingOver(attacker, victim)));
        victim.location().remove(this);
    }

}
