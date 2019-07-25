package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Blinded;
import nightgames.status.Stsflag;

public class ToggleBlindfold extends Skill {

    ToggleBlindfold() {
        super("Toggle Blindfold");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return user.is(Stsflag.blinded) ? "Remove Blindfold" : "Wear Blindfold";
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return ((!user.is(Stsflag.blinded) && user.has(Item.Blindfold))
                        || (user.is(Stsflag.blinded)) && canRemove(user)) && user.canAct();
    }

    private boolean canRemove(Character user) {
        if (!(user.getStatus(Stsflag.blinded) instanceof Blinded)) {
            return false;
        }
        Blinded status = (Blinded) user.getStatus(Stsflag.blinded);
        assert status != null;
        return status.getCause()
                     .equals("a blindfold") && status.isVoluntary();
    }

    public float priorityMod(Combat c, Character user) {
        return user.is(Stsflag.blinded) ? 4.f : -4.f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return user.is(Stsflag.blinded) ? "Remove your blindfold" : "Put on a blindfold to shield your eyes.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (!user.is(Stsflag.blinded)) {
            user.remove(Item.Blindfold);
            if (!c.getStance()
                  .sub(user) || target.roll(user, 80)) {
                user.add(c, new Blinded(user.getType(), "a blindfold", true));
                c.write(user, String.format("%s a blindfold around %s eyes.",
                                user.subjectAction("tie"), user.possessiveAdjective()));
            } else {
                c.write(user, String.format("%s out a blindfold, but %s it from %s hands and %s it away.",
                                user.subjectAction("take"), target.subjectAction("snatch", "snatches"),
                                user.possessiveAdjective(), user.action("throw")));
            }
        } else if (c.getStance()
                    .sub(user) && target.canAct() && Random.random(2) == 0) {
            c.write(user,
                            String.format("%s to take off %s blindfold, but %s %s hands away.",
                                            user.subjectAction("try", "tries"), user.possessiveAdjective(),
                                            target.subjectAction("keep"), user.possessiveAdjective()));
        } else {
            user.gain(Item.Blindfold);
            c.write(user,
                            String.format("%s off %s blindfold and %s a few times to clear %s eyes.",
                                            user.subjectAction("take"), user.possessiveAdjective(),
                                            user.action("blink"), user.possessiveAdjective()));
            user.removeStatus(Stsflag.blinded);
        }
        return true;
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
