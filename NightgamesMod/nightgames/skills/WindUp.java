package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Primed;

public class WindUp extends Skill {

    public WindUp() {
        super("Wind Up");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance()
                .mobile(user)
                        && !c.getStance()
                             .prone(user);
    }
    
    @Override
    public float priorityMod(Combat c, Character user) {
        int temp = user.getPure(Attribute.temporal);
        float base;
        if (temp < 6)
            base = 1.f;
        else if (temp < 8)
            base = 2.f;
        else if (temp < 10)
            base = 2.5f;
        else
            base = 3.f;
        return Primed.isPrimed(user, 6) ? base / 2.f : base;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Primes time charges: first charge free, 2 Mojo for each additional charge";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int charges = Math.min(4, user.getMojo()
                                           .get()
                        / 5);
        user.add(c, new Primed(user.getType(), charges + 1));
        user.spendMojo(c, charges * 5);
        writeOutput(c, Result.normal, user, target);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new WindUp();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You take advantage of a brief lull in the fight to wind up your Procrastinator, priming time charges for later use.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s fiddles with a small device on %s wrist.", user.getName(),
                        user.possessiveAdjective());
    }

}
