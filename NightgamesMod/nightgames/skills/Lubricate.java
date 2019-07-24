package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.status.Oiled;
import nightgames.status.Stsflag;

public class Lubricate extends Skill {

    Lubricate() {
        super("Lubricate");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && user.canAct() && user.has(Item.Lubricant)
                        && target.mostlyNude() && !target.is(Stsflag.oiled) && !c.getStance().prone(user);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        target.add(c, new Oiled(target.getType()));
        user.consume(Item.Lubricant, 1);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Lubricate();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You cover " + target.getName() + " with an oily Lubricant.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s throws an oily liquid at %s. The liquid "
                        + "clings to %s and makes %s whole body slippery.",
                        user.subject(), target.nameDirectObject(),
                        target.directObject(), target.possessiveAdjective());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Oil up your opponent, making her easier to pleasure";
    }

}
