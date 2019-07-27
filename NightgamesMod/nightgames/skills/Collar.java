package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.status.Collared;
import nightgames.status.Stsflag;

public class Collar extends Skill {

    public Collar() {
        super("Collar");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.trainingcollar);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().reachTop(user) && !target.canRespond()
                        && (!target.is(Stsflag.collared) || user.has(Item.Battery, 5));
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return !c.getOpponent(user).is(Stsflag.collared) ? 10 : 2;
    }
    
    @Override
    public String getLabel(Combat c, Character user) {
        return c.getOpponent(user).is(Stsflag.collared) ? "Recharge Collar" : "Place Collar";
    }
    
    @Override
    public String describe(Combat c, Character user) {
        return c.getOpponent(user).is(Stsflag.collared)
                        ? "Spend 5 batteries to recharge the collar around your opponent's neck." 
                        : "Place a Training Collar around your opponent's neck";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (target.is(Stsflag.collared)) {
            user.consume(Item.Battery, 5);
            ((Collared) target.getStatus(Stsflag.collared)).recharge();
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:replace|replaces} the batteries"
                            + " of {other:name-possessive} collar, so it can keep going for longer.",
                            user, target));
        } else {
            c.write(user, Formatter.format("Able to take {self:possessive} time - given"
                            + " {other:name-possessive} current situation - {self:subject-action:pull|pulls}"
                            + " out a metal collar and {self:action:lock|locks} it in place around"
                            + " {other:name-possessive} neck. <i>\"Is that comfortable, {other:name}?\"</i>"
                            + " {self:pronoun-action:ask|asks} {other:direct-object}, <i>\"That little"
                            + " collar is going to make sure you behave. You can be a good %s, right {other:name}?\"<i>"
                            , user, target, target.boyOrGirl()));
            target.add(c, new Collared(target.getType(), user.getType()));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
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
