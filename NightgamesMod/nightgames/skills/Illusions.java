package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Alluring;
import nightgames.status.Distorted;

public class Illusions extends Skill {

    public Illusions() {
        super("Illusions");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.spellcasting) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Create illusions to act as cover: 20 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new Distorted(user.getType(), 6));
        user.add(c, new Alluring(user.getType(), 5));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You cast an illusion spell to create several images of yourself. At the same time, you add a charm to make yourself irresistible.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s casts a brief spell and %s vision is filled with "
                        + "naked copies of %s. %s can still tell which %s is real,"
                        + " but it's still a distraction. At the same "
                        + "time, %s suddenly looks irresistible.", user.subject(),
                        target.nameOrPossessivePronoun(), user.directObject(),
                        Formatter.capitalizeFirstLetter(target.pronoun()), user.getName(),
                        user.nameDirectObject());
    }
}
