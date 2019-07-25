package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.stance.Stance;

public class ViceGrip extends Tighten {
    ViceGrip() {
        super("Vice");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.ninjutsu) >= 24;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target) && (target.stunned() || target.getStamina().percent() < 25) && target.getArousal().percent() >= 50;
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] result = new int[2];

        int m = target.getArousal().max();
        result[0] = m;
        result[1] = 1;

        return result;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(c, user, target);
        BodyPart targetO = getTargetOrgan(c, user, target);
        Result result = Result.normal;

        writeOutput(c, result, user, target);

        int[] m = getDamage(c, user, target);
        assert (m.length >= 2);

        if (m[0] != 0)
            target.body.pleasure(user, selfO, targetO, m[0], c, new SkillUsage<>(this, user, target));
        if (m[1] != 0)
            user.body.pleasure(target, targetO, selfO, m[1], -10000, c, false, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (c.getStance().en == Stance.anal) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:rhythmically squeeze|rhythmically squeezes} {self:possessive} {self:body-part:ass} around {other:possessive} dick, milking {other:direct-object} for all that {self:subject-action:are|is} worth.",
                            user, target);
        } else {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:give|gives} {other:direct-object} a seductive wink and suddenly {self:possessive} {self:body-part:pussy} squeezes around {other:possessive} {other:body-part:cock} as though it's trying to milk {other:direct-object}.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Ninjutsu technique: squeezes your opponent's dick like a vice, 100% chance to make him cum, but can only be used when the opponent is stunned or weak.";
    }

    @Override
    public String getName(Combat c, Character user) {
        return "Vice";
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
