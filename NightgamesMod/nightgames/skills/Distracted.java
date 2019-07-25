package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Stsflag;

public class Distracted extends Skill {

    public Distracted() {
        super("Distracted");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return false;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.distracted() && !user.is(Stsflag.enthralled);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You miss your opportunity to act.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        return attacker.getName() + " looks a little unfocused and makes no attempt to defend herself.";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Caught off guard";
    }

}
