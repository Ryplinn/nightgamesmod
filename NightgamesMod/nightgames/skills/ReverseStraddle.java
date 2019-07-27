package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.ReverseMount;

public class ReverseStraddle extends Skill {
    ReverseStraddle() {
        super("Mount(Reverse)");
        addTag(SkillTag.positioning);
        addTag(SkillTag.petDisallowed);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && c.getStance().mobile(target) && c.getStance().prone(target)
                        && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new ReverseMount(user.getType(), target.getType()), user, true);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You straddle " + target.getName() + ", facing her feet.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s sits on %s chest, facing %s crotch.",
                        user.subject(), target.nameOrPossessivePronoun(),
                        target.possessiveAdjective());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Straddle facing groin";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
