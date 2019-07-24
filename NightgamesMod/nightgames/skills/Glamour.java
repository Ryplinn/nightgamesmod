package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;

public class Glamour extends Skill {

    public Glamour() {
        super("Glamour");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.spellcasting) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use illusions to make yourself appear more beautiful: 15 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new nightgames.status.Glamour(user.getType(), 10));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Glamour();
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
