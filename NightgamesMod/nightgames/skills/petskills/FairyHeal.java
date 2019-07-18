package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

public class FairyHeal extends SimpleMasterSkill {
    public FairyHeal(CharacterType self) {
        super("Fairy Heal", self);
        addTag(SkillTag.heal);
    }

    @Override
    public int getMojoCost(Combat c) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), accuracy(c, target))) {
            int m = Random.random(7, 14) + getSelf().getLevel();
            c.write(getSelf(), Formatter.format("{self:SUBJECT} flies around {other:name-do}, rains magic energy on {other:direct-object}, restoring {other:possessive} strength.", getSelf(), target));
            target.heal(c, m);
        } else {
            c.write(getSelf(), Formatter.format("{self:SUBJECT} flies around the edge of the fight looking for an opening to heal {self:possessive} master.", getSelf(), target));
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new FairyHeal(user.getType());
    }
}
