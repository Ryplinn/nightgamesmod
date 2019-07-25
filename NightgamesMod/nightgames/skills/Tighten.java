package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

public class Tighten extends Thrust {
    public Tighten(String name) {
        super(name);
        removeTag(SkillTag.pleasureSelf);
    }

    public Tighten() {
        this("Tighten");
    }

    @Override
    public BodyPart getSelfOrgan(Combat c, Character user, Character target) {
        if (c.getStance().anallyPenetratedBy(c, user, target)) {
            return user.body.getRandom("ass");
        } else if (c.getStance().vaginallyPenetratedBy(c, user, target)) {
            return user.body.getRandomPussy();
        } else {
            return null;
        }
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 26 || user.has(Trait.tight);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target);
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] result = new int[2];

        int m = 5 + Random.random(10) + Math.min(user.getAttribute(Attribute.power) / 3, 20);
        result[0] = m;
        result[1] = 1;

        return result;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(c, user, target);
        BodyPart targetO = getTargetOrgan(c, user, target);
        Result result;
        if (c.getStance().en == Stance.anal) {
            result = Result.anal;
        } else {
            result = Result.normal;
        }

        writeOutput(c, result, user, target);

        int[] m = getDamage(c, user, target);
        assert (m.length >= 2);

        if (m[0] != 0)
            target.body.pleasure(user, selfO, targetO, m[0], c, new SkillUsage<>(this, user, target));
        if (m[1] != 0)
            user.body.pleasure(target, targetO, selfO, m[1], 0, c, false, new SkillUsage<>(this, user, target));
        if (selfO.isType("ass") && Random.random(100) < 2 + user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .25));
        }
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (c.getStance().en == Stance.anal) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:rhythmically squeeze|rhythmically squeezes} {self:possessive} {self:body-part:ass} around {other:possessive} dick, milking {other:direct-object} for all that {self:pronoun-action:are|is} worth.",
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
        return "Squeeze opponent's dick, no pleasure to self";
    }

    @Override
    public String getName(Combat c, Character user) {
        return "Tighten";
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
