package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class CounterPin extends CounterBase {
    CounterPin() {
        super("Counter", 4, "{self:SUBJECT-ACTION:hold|holds} a low stance.");
        addTag(SkillTag.positioning);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) Random.randomdouble();
    }

    @Override
    public void resolveCounter(Combat c, Character user, Character target) {
        Restrain skill = new Restrain();
        skill.resolve(c, user, target, true);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance().dom(user) && !c.getStance().dom(target) && user.canAct() && target.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Sets up a counter";
    }

    @Override
    public Skill copy(Character user) {
        return new CounterPin();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup && user.hasPussy()) {
            return Formatter.format("You shift into a low stance, beckoning her inside your reach.", user, target);
        } else {
            return "";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.setup && user.hasPussy()) {
            return Formatter.format("Eyeing {other:name-do} carefully, {self:SUBJECT} shifts to a low stance.", user, target);
        } else {
            return "";
        }
    }
}
