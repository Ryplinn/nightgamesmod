package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class FairyEnergize extends SimpleMasterSkill {
    public FairyEnergize() {
        super("Fairy Energize", 5);
        addTag(SkillTag.buff);
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
            int m = Random.random(17, 24) + user.getLevel() / 2;
            c.write(user, Formatter.format("{self:SUBJECT} flies around {other:name-do}, channeling energy into {other:direct-object}.", user, target));
            target.buildMojo(c, m, " (" +user.getName()+ ")");
        } else {
            c.write(user, Formatter
                            .format("{self:SUBJECT} flies around the edge of the fight looking for an opening.", user, target));
            return false;
        }
        return true;
    }

}
