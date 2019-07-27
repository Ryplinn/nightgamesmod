package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.IceStance;
import nightgames.status.Stsflag;

public class IceForm extends Skill {

    IceForm() {
        super("Ice Form");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.ki) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance().sub(user) && !user.is(Stsflag.form);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Improves resistance to pleasure, reduces mojo gain to a quarter.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (!target.is(Stsflag.blinded))
                c.write(user, receive(c, 0, Result.normal, user, target));
            else 
                printBlinded(c, user);
        }
        user.add(c, new IceStance(user.getType()));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You visualize yourself at the center of a raging snow storm. You can already feel yourself start to go numb.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s takes a deep breath and %s expression turns so "
                        + "frosty that %s not sure %s can ever thaw her out.",
                        user.subject(), user.possessiveAdjective(),
                        target.subjectAction("are", "is"), target.pronoun());
    }

}
