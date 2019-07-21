package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.PressurePointed;
import nightgames.status.Stsflag;
import nightgames.utilities.MathUtils;

public class PressurePoint extends Skill {
    PressurePoint(CharacterType self) {
        super("Pressure Point", self, 6);
        addTag(SkillTag.debuff);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 30;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().reachBottom(getSelf()) && !target.is(Stsflag.pressurepoint) && c.getStance().distance() < 2;
    }

    @Override
    public String describe(Combat c) {
        return "Attack your opponent's pressure point to make them cum instantly: 20% Stamina";
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        double kiMod = 4 * Math.sqrt(getSelf().get(Attribute.ki));
        double accuracy = kiMod + 60;
        return (int) Math.round(MathUtils.clamp(accuracy, 25, 100));
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
            target.add(c, new PressurePointed(target.getType()));
            getSelf().weaken(c, getSelf().getStamina().max() / 5);
            return true;
        } else {
            writeOutput(c, Result.miss, target);
            getSelf().weaken(c, getSelf().getStamina().max() / 5);
            return false;
        }
    }

    @Override
    public Skill copy(Character user) {
        return new PressurePoint(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return receive(c, damage, modifier, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("{self:SUBJECT-ACTION} reaches over to {other:name-possessive} lower body and {self:action:try} to drive {self:possessive} thumb into {other:possessive} stomach. "
                            + "Afraid of the consequences, {self:pronoun-action:bat} {other:possessive} hands away immediately.", getSelf(), target);
        } else {
            return Formatter.format("{self:SUBJECT-ACTION} reaches over to {other:name-possessive} lower body and {self:action:drive} {self:possessive} thumb into {other:possessive} soft stomach. {self:SUBJECT-ACTION:grin} and {self:action:say} in a cheesy voice, <i>\"You, have already cum.\"</i>", getSelf(), target);
        }
    }
}
