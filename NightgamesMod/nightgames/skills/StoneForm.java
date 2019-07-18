package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.StoneStance;
import nightgames.status.Stsflag;

public class StoneForm extends Skill {

    StoneForm(CharacterType self) {
        super("Stone Form", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && !c.getStance().sub(getSelf()) && !getSelf().is(Stsflag.form);
    }

    @Override
    public String describe(Combat c) {
        return "Improves Pain Resistance rate at expense of Speed";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        getSelf().add(c, new StoneStance(self));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new StoneForm(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You tense your body to absorb and shrug off attacks.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return String.format("%s braces %s to resist %s attacks.",
                        getSelf().subject(), getSelf().reflectivePronoun(),
                        target.nameOrPossessivePronoun());
    }

}
