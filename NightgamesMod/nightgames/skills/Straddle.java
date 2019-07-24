package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Mount;

public class Straddle extends Skill {

    public Straddle() {
        super("Mount");
        addTag(SkillTag.positioning);
        addTag(SkillTag.petDisallowed);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {

        return c.getStance().mobile(user) && c.getStance().mobile(target) && c.getStance().prone(target)
                        && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new Mount(user.getType(), target.getType()), user, true);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Straddle();
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
        return "You straddle " + target.getName() + " using your body weight to hold her down.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s plops %s down on top of %s stomach.",
                        user.subject(), user.reflectivePronoun(),
                        target.nameOrPossessivePronoun());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Straddles opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
