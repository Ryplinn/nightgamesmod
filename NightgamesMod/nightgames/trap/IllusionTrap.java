package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.IEncounter;
import nightgames.global.Global;
import nightgames.status.Flatfooted;

public class IllusionTrap extends Trap {

    public IllusionTrap() {
        this(null);
    }
    
    public IllusionTrap(Character owner) {
        super("Illusion Trap", owner);
    }

    public void setStrength(Character user) {
        setStrength(user.get(Attribute.Arcane) + user.getLevel() / 2);
    }

    @Override
    public void trigger(Character target) {
        if (target.human()) {
            Global.gui().message(
                            "You run into a girl you don't recognize, but she's beautiful and completely naked. You don't have a chance to wonder where she came from, because "
                                            + "she immediately presses her warm, soft body against you and kisses you passionately. She slips down a hand to grope your crotch, and suddenly vanishes after a few strokes. "
                                            + "She was just an illusion, but your arousal is very real.");
        } else if (target.location().humanPresent()) {
            Global.gui().message("There's a flash of pink light and " + target.name() + " flushes with arousal.");
        }
        if (target.has(Trait.imagination)) {
            target.tempt(25 + getStrength());
        }
        target.tempt(25 + getStrength());
        target.location().opportunity(target, this);
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.canSpend(15);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Arcane) >= 5;
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.spendMojo(null, 15);
        return "You cast a simple spell that will trigger when someone approaches; an illusion will seduce the trespasser.";
    }

    @Override
    public void capitalize(Character attacker, Character victim, IEncounter enc) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        enc.engage(new Combat(attacker,victim,attacker.location()));
        victim.location().remove(this);
    }

}
