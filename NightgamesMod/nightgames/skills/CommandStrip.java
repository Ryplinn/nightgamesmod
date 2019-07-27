package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;

public class CommandStrip extends PlayerCommand {

    CommandStrip() {
        super("Force Strip Self");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !target.mostlyNude();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Force your opponent to strip naked.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        target.undress(c);
        if (target.human()) {
            c.write(user, receive(c, 0, Result.normal, user, target));
        } else {
            c.write(user, deal(c, 0, Result.normal, user, target));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "You look " + target.getName() + " in the eye, sending a psychic command for"
                        + " her to strip. She complies without question, standing before you nude only"
                        + " seconds later.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The Silver Bard: CommandStrip-receive>>";
    }

}
