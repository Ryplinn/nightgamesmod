package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;

public class CommandStripPlayer extends PlayerCommand {

    CommandStripPlayer() {
        super("Force Strip Player");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !user.mostlyNude();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Make your thrall undress you.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.undress(c);
        if (user.human()) {
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
        return "With an elated gleam in her eyes, " + target.getName()
                        + " moves her hands with nigh-inhuman dexterity, stripping all"
                        + " of your clothes in just a second.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandStripPlayer-receive>>";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
