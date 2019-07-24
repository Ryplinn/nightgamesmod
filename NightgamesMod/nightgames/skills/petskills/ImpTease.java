package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public class ImpTease extends SimpleEnemySkill {
    public ImpTease() {
        super("Imp Tease");
        addTag(SkillTag.pleasure);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return super.requirements(c, user, target) && gendersMatch(user, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = (int) (Random.random(10, 16) + Math.sqrt(user.getLevel())) / 2;
            if (target.crotchAvailable() && !c.getStance().penisInserted(target) && target.hasDick()) {
                c.write(user, Formatter.format("{self:SUBJECT} jumps onto {other:name-do} and humps at {other:possessive} dick before "
                                + "{other:subject-action:can pull|can pull} {self:direct-object} off.",
                                    user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(), m, c);
                return true;
            } else if (target.hasPussy() && !c.getStance().vaginallyPenetrated(c, target) && target.crotchAvailable() && user.hasDick()) {
                c.write(user, Formatter.format("{self:SUBJECT} latches onto {other:name-do} and shoves {self:possessive} thick cock into {other:possessive} pussy. As the demon humps {other:direct-object}, {other:SUBJECT-ACTION:yell|shrieks} and punches {self:direct-object} away.",
                                user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomPussy(), m, c);
                return true;
            } else if (target.breastsAvailable() || target.isPet()) {
                c.write(user, Formatter.format("{self:SUBJECT} jumps up and hugs {other:name-possessive} chest and licks {other:possessive} nipples with "
                                + "{self:possessive} longer than average tongue until {other:pronoun-action:pull|pulls} {self:direct-object} off.",
                                user, target));
                m += 5;
                target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandomBreasts(), m, c);
                return true;
            }
        }
        c.write(user, Formatter.format("{self:SUBJECT} stands at the periphery of the fight, touching {self:reflective} idly.", user, target));
        return false;
    }

    @Override
    public Skill copy(Character user) {
        return new ImpTease();
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
