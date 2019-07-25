package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Shield;

public class FairyShield extends SimpleMasterSkill {
    public FairyShield() {
        super("Fairy Shield", 10);
        addTag(SkillTag.buff);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int duration = 3 + user.getLevel() / 10;
            c.write(user, Formatter.format("{self:SUBJECT} raises a shield around {other:name-do}, preventing attacks!", user, target));
            target.add(c, new Shield(target.getType(), .5, duration));
        } else {
            c.write(user, Formatter
                            .format("{self:SUBJECT} flies around the edge of the fight looking for an opening.", user, target));
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

}
