package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.StandingOver;

public class StandUp extends Skill {

    public StandUp() {
        super("Stand Up");
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().getUp(user) && !c.getStance().mobile(target)
                        && !c.getStance().havingSex(c, user);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new StandingOver(user.getType(), target.getType()), user, true);
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
        return Tactics.positioning;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -2;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You relinquish your hold on " + target.getName() + " and stand back up.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s relinquishes %s hold on %s and stands back up.",
                        user.subject(), user.possessiveAdjective(),
                        target.nameDirectObject());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Stand up";
    }
}
