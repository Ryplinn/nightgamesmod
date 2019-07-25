package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;

public class ReverseFuck extends Fuck {
    private ReverseFuck(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.positioning);
    }

    public ReverseFuck() {
        this("Reverse Fuck", 0);
    }

    @Override
    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandomPussy();
    }

    @Override
    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomCock();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target);
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        if (modifier == Result.normal) {
            return Formatter.format(
                            "{self:subject-action:rub|rubs} {self:possessive} {self:body-part:pussy} against {other:possessive} {other:body-part:cock}, "
                                            + "causing {other:direct-object} to shiver with anticipation. In one swift motion, {self:subject-action:plunge|plunges} {other:possessive} {other:body-part:cock} "
                                            + "into {self:possessive} depths.",
                            user, target);
        } else if (modifier == Result.miss) {
            if (!selfO.isReady(user) && !targetO.isReady(target)) {
                return Formatter.format(
                                "{self:subject-action:are|is} in a good position to fuck {other:direct-object}, but neither of %s are aroused enough to follow through.",
                                user, target, c.bothDirectObject(target));
            } else if (!getTargetOrgan(target).isReady(target)) {
                return Formatter.format(
                                "{self:subject-action:position|positions} {self:possessive} {self:body-part:pussy} on top of {other:possessive} {other:body-part:cock}, "
                                                + "but {self:subject-action:find|finds} that {other:possessive} {other:body-part:cock} is still limp.",
                                user, target);
            } else if (!selfO.isReady(user)) {
                return Formatter.format(
                                "{self:subject-action:position|positions} {self:possessive} {self:body-part:pussy} on top of {other:possessive} {other:body-part:cock}, "
                                                + "but {self:subject-action|find:finds} that {self:subject-action:are:is} not nearly wet enough to allow a comfortable insertion.",
                                user, target);
            }
            return Formatter.format("{self:subject-action:manage|manages} to miss the mark.", user, target);
        }
        return "Bad stuff happened";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Straddle your opponent and ride " + c.getOpponent(user).possessiveAdjective() + " cock";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
