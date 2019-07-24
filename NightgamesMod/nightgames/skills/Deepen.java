package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Enthralled;
import nightgames.status.Lovestruck;
import nightgames.status.Stsflag;
import nightgames.status.Trance;

public class Deepen extends Skill {

    Deepen() {
        super("Deepen");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.hypnotism) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().behind(user)
                        && !c.getStance().behind(target) && !c.getStance().sub(user)
                        && !target.is(Stsflag.enthralled)
                        && (target.is(Stsflag.trance) || target.is(Stsflag.lovestruck) || target.is(Stsflag.charmed));
    }
    
    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Deepen your opponent's trance";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.is(Stsflag.trance)) {
            if (target.human()) {
                c.write(user, Formatter.format("{self:NAME-POSSESSIVE} all-encompassing eyes completely fills your field of vision now as she destroys any last trace of independent thought inside your mind.", user, target));
            } else {
                c.write(user, Formatter.format("Since {other:NAME-DO} has already been heavily hypnotized, you take the chance to erode the last bits of {other:possessive} resistance. There's no way {other:pronoun} can disobey you now.", user, target));
            }
            target.add(c, new Enthralled(target.getType(), user.getType(), 3));
        } else if (target.is(Stsflag.lovestruck)) {
            if (target.human()) {
                c.write(user, Formatter.format("{self:SUBJECT} holds your face in her hands and forces you to look into her eyes. You don't even think about resisting as her words become truth inside your brain.", user, target));
            } else {
                c.write(user, Formatter.format("Since {other:NAME-DO} has already been hypnotized, you take the chance to bring {other:direct-object} even deeper.", user, target));
            }
            target.add(c, new Trance(target.getType(), 4));
        } else if (target.is(Stsflag.charmed)) {
            if (target.human()) {
                c.write(user, Formatter.format("{self:SUBJECT} leans close and brings you deeper under her control with her hypnotic voice.", user, target));
            } else {
                c.write(user, Formatter.format("Since {other:NAME-DO} has already been lightly hypnotized, you take the chance to bring {other:direct-object} deeper.", user, target));
            }
            target.add(c, new Lovestruck(target.getType(), user.getType(), 5));
        }
        return false;
    }

    @Override
    public Skill copy(Character user) {
        return new Deepen();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "NA";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "NA";
    }
}
