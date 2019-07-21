package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Horny;

public class ImpSemenSquirt extends SimpleEnemySkill {
    public ImpSemenSquirt(CharacterType self) {
        super("Imp Semen Squirt", self);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return super.usable(c, target) && getSelf().hasDick()
                        && c.getStance().faceAvailable(target) 
                        && gendersMatch(target);
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m = Random.random(3,6) + getSelf().getLevel() / 5;
        c.write(getSelf(), Formatter.format("{self:SUBJECT} masturbates frantically until {self:pronoun} cums intensely. "
                        + "{self:PRONOUN} aims {self:possessive} spurting cock at {other:name-do}, "
                        + "hitting {other:direct-object} in the face with a thick load of semen. "
                        + "{other:SUBJECT-ACTION:flush|flushes} bright red and {other:action:look|looks} stunned "
                        + "as the aphrodisiac laden fluid overwhelms {other:possessive} senses.", getSelf(), target));
        getSelf().body.pleasure(getSelf(), getSelf().body.getRandom("hands"), getSelf().body.getRandomCock(), 10, c);
        target.add(c, new Horny(target.getType(), m, 5, "imp cum"));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ImpSemenSquirt(user.getType());
    }

    @Override
    public int speed() {
        return 8;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
