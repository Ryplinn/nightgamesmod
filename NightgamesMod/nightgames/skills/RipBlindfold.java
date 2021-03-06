package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Blinded;
import nightgames.status.Stsflag;

public class RipBlindfold extends Skill {

    RipBlindfold() {
        super("Rip Blindfold");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance()
                                      .reachTop(user)
                        && target.is(Stsflag.blinded) && target.getStatus(Stsflag.blinded) instanceof Blinded;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        if (!user.human() && user.has(Trait.mindcontroller)) {
            return c.getStance().dom(user) ? 10.f : 2.f;
        }
        return -5.f;
    }
    
    @Override
    public String describe(Combat c, Character user) {
        return "Rip your opponent's blindfold off.";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        if (!target.canAct() || !((Blinded) target.getStatus(Stsflag.blinded)).isVoluntary()) {
            return 200;
        }
        int base = 60;
        if (c.getStance().sub(target)) {
            base = 100 - (base / 2);
        }
        if (c.getStance().penetratedBy(c, target, user)) {
            base = 100 - (base / 3);
        }
        return base;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {

        if (rollSucceeded) {
            c.write(user,
                            String.format("%s %s blindfold and %s it off with a strong yank.",
                                            user.subjectAction("grab"), target.nameOrPossessivePronoun(),
                                            user.action("pull")));
            target.removeStatus(Stsflag.blinded);
        } else {
            c.write(user, String.format("%s at %s blindfold, but %s %s away from %s fingers.",
                            user.subjectAction("grasp"), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("twist"), user.possessiveAdjective()));
        }

        return rollSucceeded;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
