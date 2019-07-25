package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Slimed;
import nightgames.status.Stsflag;

public class ShakeOff extends Skill {

    private ShakeOff() {
        super("Shake Off");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.is(Stsflag.slimed) && user.canAct() && c.getStance().mobile(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Shake off some of that slime.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new Slimed(user.getType(), target.getType(), -10));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:SUBJECT-ACTION:take|takes} a moment to shake off the sticky slime all over {self:reflective}", user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }
}
