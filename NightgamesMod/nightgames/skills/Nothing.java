package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;

public class Nothing extends Skill {

    public Nothing() {
        super("Nothing");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            deal(c, 0, Result.normal, user, target);
        } else {
            receive(c, 0, Result.normal, user, target);
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You are unable to do anything.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.subject() + "is unable to do anything.";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Do nothing";
    }
}
