package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.BondageFetish;
import nightgames.status.Stsflag;

public class Bondage extends Skill {

    public Bondage() {
        super("Bondage");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.fetishism) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && c.getStance().mobile(user) && user.getArousal().get() >= 5
                        && !user.is(Stsflag.bondage);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "You and your opponent become aroused by being tied up for five turns: Arousal at least 5";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new BondageFetish(user.getType()));
        target.add(c, new BondageFetish(target.getType()));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You imagine the exhilarating feeling of ropes digging into your skin and binding you. You push this feeling into "
                        + target.getName() + "'s libido.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName()
                        + " flushes and wraps her arms around herself tightly. Suddenly the thought of being tied up and dominated slips into "+target.nameOrPossessivePronoun()+" head.";
    }

}
