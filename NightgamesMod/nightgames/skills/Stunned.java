package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;

public class Stunned extends Skill {
    public Stunned() {
        super("Stunned");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.stunned();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (Random.random(3) >= 2) {
                c.write(user, user.stunLiner(c, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Skill copy(Character user) {
        return new Stunned();
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
        return "You're unable to move.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s is on the floor, trying to catch %s breath.",
                        user.subject(), user.possessiveAdjective());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "You're stunned";
    }
}
