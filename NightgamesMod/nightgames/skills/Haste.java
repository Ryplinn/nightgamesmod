package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.AttributeBuff;
import nightgames.status.Primed;

public class Haste extends Skill {

    Haste() {
        super("Haste", 6);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance()
                .mobile(user)
                        && !c.getStance()
                             .prone(user)
                        && user.canAct() && Primed.isPrimed(user, 1);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Temporarily buffs your speed: 1 charge";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Primed(user.getType(), -1));
        user.add(c, new AttributeBuff(user.getType(), Attribute.speed, 10, 6));
        writeOutput(c, Result.normal, user, target);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Haste();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You spend a stored time charge. The world around you appears to slow down as your personal time accelerates.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s hits a button on %s wristwatch and suddenly speeds up. %s is moving so fast that %s seems to blur.",
                        user.getName(), user.possessiveAdjective(), user.pronoun(), user.pronoun());
    }

}
