package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.status.Blinded;
import nightgames.status.Stsflag;

public class PlaceBlindfold extends Skill {

    PlaceBlindfold() {
        super("Place Blindfold");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && user.has(Item.Blindfold) && !target.is(Stsflag.blinded) && !c.getStance()
                                                                                 .mobile(target);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        if (!user.human() && user.has(Trait.mindcontroller)) {
            return -3.f;
        }
        return 2.f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Place a blindfold over your opponent's eyes";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return target.canAct() ? 200 : 60;
    }
    
    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            c.write(user,
                            String.format("%s a blindfold around %s head, covering %s eyes.",
                                            user.subjectAction("snap"), target.nameOrPossessivePronoun(),
                                            target.possessiveAdjective()));
            user.remove(Item.Blindfold);
            target.add(c, new Blinded(target.getType(), "a blindfold", false));
        } else {
            c.write(user,
                            String.format("%s out a blindfold and %s to place it around %s "
                                            + "head, but %s it away and throws it clear.",
                                            user.subjectAction("take"), user.action("try", "tries"),
                                            target.possessiveAdjective(),
                                            target.subjectAction("rip")));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
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
