package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

public class OrgasmicTighten extends Thrust {
    public OrgasmicTighten() {
        super("Orgasmic Tighten");
        removeTag(SkillTag.pleasureSelf);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return false;
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
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] result = new int[2];

        int m = Random.random(25, 40) + Math.min(user.get(Attribute.power) / 3, 20);
        result[0] = m;

        return result;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(c, user, target);
        BodyPart targetO = getTargetOrgan(c, user, target);
        Result result;
        if (c.getStance().anallyPenetratedBy(c, user, target)) {
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
            user.body.pleasure(target, targetO, selfO, m[1], -10000, c, false, new SkillUsage<>(this, user, target));
        if (selfO.isType("ass") && Random.random(100) < 2 + user.get(Attribute.fetishism)) {
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
            return Formatter.format("While cumming {self:name-possessive} spasming backdoor seems to urge {other:name-do} to do the same.",
                            user, target);
        } else {
            return Formatter.format("While cumming {self:name-possessive} spasming honeypot seems to urge {other:name-do} to do the same.",
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
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
