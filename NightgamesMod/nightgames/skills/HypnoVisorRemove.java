package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Stsflag;

public class HypnoVisorRemove extends Skill {

    HypnoVisorRemove() {
        super("Remove Hypno Visor");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return (int) ((Math.min(0.8, .2 + user.getAttribute(Attribute.cunning) / 100.0)) * 100);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && user.is(Stsflag.hypnovisor) && c.getStance().mobile(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Try to remove the Hypno Visor from your head.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:find|finds} a small button"
                            + " on the side of the Hypno Visor, and pressing it unlocks whatever"
                            + " mechanisms held it in place. {self:PRONOUN-ACTION:make|makes} sure"
                            + " to throw it far away before refocusing on the fight.", user, target));
            user.removeStatus(Stsflag.hypnovisor);
            return true;
        }
        c.write(user, Formatter.format("{self:SUBJECT-ACTION:claw|claws} at the insidious visor"
                        + " around {self:possessive} head, but to no avail.", user, target));
        return false;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
