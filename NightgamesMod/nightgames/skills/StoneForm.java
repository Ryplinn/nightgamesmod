package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.StoneStance;
import nightgames.status.Stsflag;

public class StoneForm extends Skill {

    StoneForm() {
        super("Stone Form");
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
        return "Improves Pain Resistance rate at expense of Speed";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new StoneStance(user.getType()));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You tense your body to absorb and shrug off attacks.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s braces %s to resist %s attacks.",
                        user.subject(), user.reflectivePronoun(),
                        target.nameOrPossessivePronoun());
    }

}
