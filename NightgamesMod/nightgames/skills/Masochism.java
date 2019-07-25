package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Masochistic;
import nightgames.status.Stsflag;

public class Masochism extends Skill {

    public Masochism() {
        super("Masochism");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.fetishism) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && user.getArousal().get() >= 15
                        && !user.is(Stsflag.masochism);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "You and your opponent become aroused by pain: Arousal at least 15";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new Masochistic(user.getType()));
        target.add(c, new Masochistic(target.getType()));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You fantasize about the pleasure that exquisite pain can bring. You share this pleasure with "
                        + target.getName() + ".";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s shivers in arousal. %s suddenly bombarded with thoughts of "
                        + "letting %s hurt %s in wonderful ways.", user.subject(),
                        target.subjectAction("are", "is"), user.subject(), target.directObject());
    }

}
