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
    public KiShout(Character self) {
        super("Ki Shout", self, 3);
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !target.wary() && !c.getStance().sub(getSelf()) && !c.getStance().prone(getSelf())
                        && !c.getStance().prone(target) && getSelf().canAct();
    }

    @Override
    public String describe(Combat c) {
        return "Overwhelm your opponent with a loud shout, 25% stamina";
    }

    @Override
    public int getMojoCost(Combat c) {
        return 15;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        double attDifference = (2 * getSelf().get(Attribute.ki) + getSelf().get(Attribute.power)) - target.get(Attribute.power);
        double accuracy = 2.5f * attDifference + 75 - target.knockdownDC();
        return (int) Math.round(MathUtils.clamp(accuracy, 25, 150));
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
            target.pain(c, getSelf(), (int) (10 + 3 * Math.sqrt(getSelf().get(Attribute.ki))));
            target.add(c, new Falling(target));
            getSelf().weaken(c, getSelf().getStamina().max() / 4);
            return true;
        } else {
            writeOutput(c, Result.miss, target);
            target.pain(c, getSelf(), (int) (10 + 3 * Math.sqrt(getSelf().get(Attribute.ki))));
            getSelf().weaken(c, getSelf().getStamina().max() / 4);
            return false;
        }
    }

    @Override
    public Skill copy(Character user) {
        return new KiShout(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return receive(c, damage, modifier, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("{self:SUBJECT-ACTION:take} a deep breath, gathering {self:possessive} ki in {self:possessive} center. "
                            + "Without warning, {self:subject-action:let} out an earsplitting howl that forces {other:name-do} back several feet. "
                            + "Unfortunately {other:pronoun-action:recover} quite quickly.", getSelf(), target);
        } else {
            return Formatter.format("{self:SUBJECT-ACTION:take} a deep breath, gathering {self:possessive} ki in {self:possessive} center. "
                            + "Without warning, {self:subject-action:let} out an earsplitting howl that knocks {other:name-do} off {other:possessive} feet.", getSelf(), target);
        }
    }
}
