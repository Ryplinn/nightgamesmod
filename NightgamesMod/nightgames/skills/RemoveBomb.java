package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Compulsive;
import nightgames.status.Compulsive.Situation;
import nightgames.status.Stsflag;

import java.util.Optional;

public class RemoveBomb extends Skill {

    RemoveBomb() {
        super("Remove Bomb");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && user.is(Stsflag.bombed);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Try to remove the device on your chest.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Optional<String> compulsion = Compulsive.describe(c, user, Situation.PREVENT_REMOVE_BOMB);
        if (compulsion.isPresent() && Random.random(100) < 40) {
            c.write(user, compulsion.get());
            user.pain(c, null, 20 + Random.random(40));
            Compulsive.doPostCompulsion(c, user, Situation.PREVENT_REMOVE_BOMB);
            return false;
        }
        if (c.getStance().dom(target)) {
            if (Random.random(100) < 75) {
                writeOutput(c, Result.miss, user, target);
                return false;
            } else {
                writeOutput(c, Result.normal, user, target);
                user.removeStatus(Stsflag.bombed);
            }
        } else if (Random.random(100) < user.getStamina().percent()) {
            writeOutput(c, Result.normal, user, target);
            user.removeStatus(Stsflag.bombed);
        } else {
            writeOutput(c, Result.miss, user, target);
        }
        user.pain(c, null, 10 + Random.random(40));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return "You grab the beeping device on your chest and rip it off. It gives off"
                            + " a powerful shock, but you ignore it long enough to throw"
                            + " it away.";
        }
        if (modifier == Result.special) {
            return "You reach up to grab the sphere on your chest, but the collar around your neck"
                            + " does not appreciate the sentiment and shocks you to keep your arms down.";
        }
        if (!c.getStance().dom(target)) {
            return "You reach for the device on your chest, but the moment you touch it, it sends"
                            + " a powerful shock up your arm. You draw your hand back in pain.";
        }
        switch (c.getStance().en) {
            case behind:
            case pin:
                return Formatter.format("You reach towards your chest, aiming to get the beeping sphere off and away,"
                            + " but {other:subject} catches your wrists and pulls your hands back down."
                                , user, target);
            case missionary:
            case cowgirl:
            case mount:
                return Formatter.format("You try to get the metallic sphere off your chest, but {other:subject} catches"
                                + " your hands and pulls them up over your head, well away from the"
                                + " intimidating device.", user, target);
            default:
                return Formatter.format("You try to remove the metallic sphere from your chest, but {other:subject}"
                                + " keeps your hands away from it.", user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "{self:SUBJECT} tries to remove your bomb from {self:possessive} chest, but fails.";
        }
        return "{self:SUBJECT} removes your bomb from {self:possessive} chest.";
    }

}
