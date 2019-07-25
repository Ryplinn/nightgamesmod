package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Winded;

public class SelfStun extends Skill {

    SelfStun() {
        super("Stun Self");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !user.stunned() && user.canAct();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Stun yourself. For Debugging!";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Winded(user.getType()));
        writeOutput(c, Result.normal, user, target);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You stun yourself. Yup.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "She stuns herself. Yup.";
    }

}
