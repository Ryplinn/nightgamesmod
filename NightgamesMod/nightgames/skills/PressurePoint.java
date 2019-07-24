package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.PressurePointed;
import nightgames.status.Stsflag;
import nightgames.utilities.MathUtils;

public class PressurePoint extends Skill {
    PressurePoint() {
        super("Pressure Point", 6);
        addTag(SkillTag.debuff);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 30;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().reachBottom(user) && !target.is(Stsflag.pressurepoint) && c.getStance().distance() < 2;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attack your opponent's pressure point to make them cum instantly: 20% Stamina";
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        double kiMod = 4 * Math.sqrt(user.get(Attribute.ki));
        double accuracy = kiMod + 60;
        return (int) Math.round(MathUtils.clamp(accuracy, 25, 100));
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);
            target.add(c, new PressurePointed(target.getType()));
            user.weaken(c, user.getStamina().max() / 5);
            return true;
        } else {
            writeOutput(c, Result.miss, user, target);
            user.weaken(c, user.getStamina().max() / 5);
            return false;
        }
    }

    @Override
    public Skill copy(Character user) {
        return new PressurePoint();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return receive(c, damage, modifier, user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("{self:SUBJECT-ACTION} reaches over to {other:name-possessive} lower body and {self:action:try} to drive {self:possessive} thumb into {other:possessive} stomach. "
                            + "Afraid of the consequences, {self:pronoun-action:bat} {other:possessive} hands away immediately.", user, target);
        } else {
            return Formatter.format("{self:SUBJECT-ACTION} reaches over to {other:name-possessive} lower body and {self:action:drive} {self:possessive} thumb into {other:possessive} soft stomach. {self:SUBJECT-ACTION:grin} and {self:action:say} in a cheesy voice, <i>\"You, have already cum.\"</i>", user, target);
        }
    }
}
