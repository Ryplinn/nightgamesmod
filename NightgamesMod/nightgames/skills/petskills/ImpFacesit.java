package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Horny;

public class ImpFacesit extends SimpleEnemySkill {
    public ImpFacesit() {
        super("Imp Facesit");
        addTag(SkillTag.pleasure);
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && c.getStance().prone(target)
                        && user.hasPussy() && c.getStance().faceAvailable(target)
                        && gendersMatch(user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = Random.random(3,6) + user.getLevel() / 5;
        c.write(user, Formatter.format("{self:SUBJECT} straddles {other:name-possessive} face, forcing {self:possessive} wet pussy onto {other:possessive} nose and mouth. "
                        + "{self:POSSESSIVE} scent is unnaturally intoxicating and fires up {other:possessive} libido.", user, target));
        user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomPussy(), 10, c);
        target.add(c, new Horny(target.getType(), m, 5, "imp juices"));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ImpFacesit();
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
