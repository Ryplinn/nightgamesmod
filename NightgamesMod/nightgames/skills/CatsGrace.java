package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.stance.Stance;
import nightgames.status.Nimble;
import nightgames.status.Stsflag;

public class CatsGrace extends Skill {

    CatsGrace() {
        super("Cat's Grace");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.animism) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !user.is(Stsflag.nimble) && c.getStance().en == Stance.neutral && user.canAct() && c.getStance().mobile(user)
                        && user.getArousal().percent() >= 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use your instinct to nimbly avoid attacks";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new Nimble(user.getType(), 4));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You rely on your animal instincts to quicken your movements and avoid attacks.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName()
                        + " focuses for a moment and "+user.possessiveAdjective()
                        +" movements start to speed up and become more animalistic.";
    }

}
