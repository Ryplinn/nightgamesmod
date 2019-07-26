package nightgames.characters.trait;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.skills.Skill;
import nightgames.status.Status;

/**
 * When taunting, increases chance of applying Shamed status. When taunting while in dominant position, causes target to lose willpower.
 * Increases taunt damage.
 */
public class BitingWords extends BaseTrait {
    BitingWords() {
        super("Biting Words", "Knows how to rub salt in the wound.");
    }

    @Override public String describe(Character self) {
        return "";
    }

    @Override
    public int dealTemptBonusDamage(Combat c, Character tempter, Character target, BodyPart with, int baseDamage,
                    Skill skill) {
        if (skill != null && skill.getName(c, tempter).equals("Taunt")) {
            return 4;
        }
        return super.dealTemptBonusDamage(c, tempter, target, with, baseDamage, skill);
    }

    @Override public void onSkillUse(Skill skill, Combat c, Character user, Character target) {
        if (skill.getName(c, user).equals("Taunt") && c.getStance().dom(user)) {
            int willpowerLoss = Math.max(target.getWillpower().max() / 50, 3) + Random.random(3);
            target.loseWillpower(c, willpowerLoss, 0, false, " (Biting Words)");
        }
    }

    @Override
    public double statusChanceMultiplier(Skill skill, Combat c, Character user, Character target, Status possible) {
        if (skill.getName(c, user).equals("Taunt") && possible.name.equals("Shamed")) {
            return 1;
        }
        return super.statusChanceMultiplier(skill, c, user, target, possible);
    }

}
