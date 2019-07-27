package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class FairyHeal extends SimpleMasterSkill {
    public FairyHeal() {
        super("Fairy Heal");
        addTag(SkillTag.heal);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            int m = Random.random(7, 14) + user.getLevel();
            c.write(user, Formatter.format("{self:SUBJECT} flies around {other:name-do}, rains magic energy on {other:direct-object}, restoring {other:possessive} strength.", user, target));
            target.heal(c, m);
        } else {
            c.write(user, Formatter.format("{self:SUBJECT} flies around the edge of the fight looking for an opening to heal {self:possessive} master.", user, target));
            return false;
        }
        return true;
    }

}
