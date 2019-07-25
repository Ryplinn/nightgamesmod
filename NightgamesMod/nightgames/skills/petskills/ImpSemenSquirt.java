package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;
import nightgames.status.Horny;

public class ImpSemenSquirt extends SimpleEnemySkill {
    public ImpSemenSquirt() {
        super("Imp Semen Squirt");
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && user.hasDick()
                        && c.getStance().faceAvailable(target) 
                        && gendersMatch(user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = Random.random(3,6) + user.getLevel() / 5;
        c.write(user, Formatter.format("{self:SUBJECT} masturbates frantically until {self:pronoun} cums intensely. "
                        + "{self:PRONOUN} aims {self:possessive} spurting cock at {other:name-do}, "
                        + "hitting {other:direct-object} in the face with a thick load of semen. "
                        + "{other:SUBJECT-ACTION:flush|flushes} bright red and {other:action:look|looks} stunned "
                        + "as the aphrodisiac laden fluid overwhelms {other:possessive} senses.", user, target));
        user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomCock(), 10, c);
        target.add(c, new Horny(target.getType(), m, 5, "imp cum"));
        return true;
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
        return false;
    }
}
