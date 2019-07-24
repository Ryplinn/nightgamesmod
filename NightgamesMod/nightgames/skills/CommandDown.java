package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Mount;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;

public class CommandDown extends PlayerCommand {

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.is(Stsflag.enthralled)
                        && ((Enthralled) target.getStatus(Stsflag.enthralled)).master.equals(user.getType())
                        && !c.getStance().havingSex(c) && user.canRespond();
    }

    CommandDown() {
        super("Force Down");
        addTag(SkillTag.positioning);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Command your opponent to lie down on the ground.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.setStance(new Mount(user.getType(), target.getType()), target, false);
        writeOutput(c, Result.normal, user, target);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CommandDown();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "Trembling under the weight of your command, " + target.getName()
                        + " lies down. You follow her down and mount her, facing her head.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return String.format("%s tells %s to remain still and"
                                        + " gracefully lies down on %s, %s face right above %ss.",
                                        user.getName(), target.subject(),
                                        target.directObject(), user.possessiveAdjective(),
                                        target.possessiveAdjective());
    }
}
