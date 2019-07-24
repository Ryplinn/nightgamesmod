package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Stsflag;
import nightgames.status.WaterStance;

public class WaterForm extends Skill {

    public WaterForm() {
        super("Water Form");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance().sub(user) && !user.is(Stsflag.form);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Improves evasion and counterattack rate at expense of Power";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new WaterStance(user.getType()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new WaterForm();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You relax your muscles, prepared to flow with and counter " + target.getName() + "'s attacks.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s takes a deep breath and %s movements become much more fluid.",
                        user.subject(), user.possessiveAdjective());
    }

}
