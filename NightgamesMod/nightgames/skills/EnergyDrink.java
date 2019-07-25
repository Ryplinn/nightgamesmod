package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Stsflag;

public class EnergyDrink extends Skill {

    public EnergyDrink() {
        super("Energy Drink");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && user.canAct() && user.has(Item.EnergyDrink);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Terrible stuff, but will make you less tired";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (target.human()) {
            if (!target.is(Stsflag.blinded))
                c.write(user, receive(c, 0, Result.normal, user, target));
            else 
                printBlinded(c, user);
        } else if (c.isBeingObserved()) {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        user.heal(c, Math.max(20, user.getStamina().max() / 2));
        user.buildMojo(c, 20 + Random.random(10));

        user.consume(Item.EnergyDrink, 1);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You chug an energy drink and feel some of your fatigue vanish.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName() + " opens up an energy drink and downs the whole can.";
    }

}
