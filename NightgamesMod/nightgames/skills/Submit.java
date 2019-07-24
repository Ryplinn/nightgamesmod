package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;

public class Submit extends Skill {

    private Submit() {
        super("Submit");

    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().en == Stance.neutral && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new StandingOver(target.getType(), user.getType()), target, false);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.submissive) || user.humanControlled(c);
    }

    @Override
    public Skill copy(Character user) {
        return new Submit();
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You nervously lie down on the floor.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName() + " with a nervous glance, lies down on the floor.";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Submits to your opponent by lying down.";
    }
}
