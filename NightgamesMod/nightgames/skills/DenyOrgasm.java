package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.CockChoked;
import nightgames.status.Stsflag;

public class DenyOrgasm extends Skill {

    DenyOrgasm() {
        super("Deny Orgasm", 4);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 20 && user.has(Trait.tight);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !target.is(Stsflag.orgasmseal) && target.getArousal().percent() > 50
                        && c.getStance().penetratedBy(c, user, target) && !target.has(Trait.strapped);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Prevents your opponents from cumming by tightening around their cock";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        target.add(c, new CockChoked(target.getType(), user.getType(), 4));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You give " + target.subject() + " a quick smirk and tighten yourself around "
                        + target.possessiveAdjective() + " cock, keeping " + target.possessiveAdjective()
                        + " boiling cum from escaping";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.subject() + " gives " + target.subject() + " a quick smirk and tightens down on "
                        + target.possessiveAdjective() + " cock, keeping " + target.possessiveAdjective()
                        + " boiling cum from escaping";
    }
}
