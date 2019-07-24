package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

public class GoblinDenial extends SimpleMasterSkill {
    public GoblinDenial() {
        super("GoblinDenial", 5);
        addTag(SkillTag.calm);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = Random.random(17, 24) + user.getLevel() / 2;
            c.write(user, Formatter.format("{self:SUBJECT} suddenly appears to turn against {other:name-do} and slaps {other:direct-object} sensitive testicles. "
                            + "You're momentarily confused, but you realize the shock probably lessened some of {other:possessive} pent up desires.", user, target));
            target.pain(c, user, Random.random(15, 25));
            target.calm(c, m * 2);
        } else {
            c.write(user, Formatter.format("{self:SUBJECT} stays at the edge of battle and touches herself absentmindedly.", user, target));
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinDenial();
    }
}
