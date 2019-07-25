package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Falling;
import nightgames.utilities.MathUtils;

public class KiShout extends Skill {
    KiShout() {
        super("Ki Shout", 3);
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().sub(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && user.canAct();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Overwhelm your opponent with a loud shout, 25% stamina";
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        double attDifference = (2 * user.get(Attribute.ki) + user.get(Attribute.power)) - target.get(Attribute.power);
        double accuracy = 2.5f * attDifference + 75 - target.knockdownDC();
        return (int) Math.round(MathUtils.clamp(accuracy, 25, 150));
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);
            target.pain(c, user, (int) (10 + 3 * Math.sqrt(user.get(Attribute.ki))));
            target.add(c, new Falling(target.getType()));
            user.weaken(c, user.getStamina().max() / 4);
            return true;
        } else {
            writeOutput(c, Result.miss, user, target);
            target.pain(c, user, (int) (10 + 3 * Math.sqrt(user.get(Attribute.ki))));
            user.weaken(c, user.getStamina().max() / 4);
            return false;
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return receive(c, damage, modifier, user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("{self:SUBJECT-ACTION:take} a deep breath, gathering {self:possessive} ki in {self:possessive} center. "
                            + "Without warning, {self:subject-action:let} out an earsplitting howl that forces {other:name-do} back several feet. "
                            + "Unfortunately {other:pronoun-action:recover} quite quickly.", user, target);
        } else {
            return Formatter.format("{self:SUBJECT-ACTION:take} a deep breath, gathering {self:possessive} ki in {self:possessive} center. "
                            + "Without warning, {self:subject-action:let} out an earsplitting howl that knocks {other:name-do} off {other:possessive} feet.", user, target);
        }
    }
}
