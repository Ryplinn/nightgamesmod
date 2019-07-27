package nightgames.characters.trait;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.skills.Skill;
import nightgames.skills.Tickle;
import nightgames.status.Winded;

/**
 * Mara Sex perk. Tickling always hits. If target is nude, increases pleasure and weakness from tickling and tickling
 * may cause Winded.
 */
public class TickleMonster extends BaseTrait {
    TickleMonster() {
        super("Tickle Monster", "Skilled at tickling in unconventional areas.");
    }

    @Override public int modAccuracy(Combat c, Character user, Character target, Skill skill) {
        if (skill.getName(c, user).equals("Tickle")) {
            return 200;
        }
        return 0;
    }

    private boolean canTickleNaughtyBits(Combat c, Character tickler, Character target) {
        return target.crotchAvailable() && c.getStance().reachBottom(tickler) && !c.getStance().havingSex(c) && target
                        .mostlyNude();
    }

    @Override public int modPleasureDealt(Combat c, Character dealer, Character target, Skill skill) {
        if (skill instanceof Tickle && canTickleNaughtyBits(c, dealer, target)) {
            return 5 + Random.random(4); // 5-8 base bonus pleasure
        }
        return 0;
    }

    @Override public int modWeakenDealt(Combat c, Character dealer, Character target, Skill skill) {
        if (skill instanceof Tickle && canTickleNaughtyBits(c, dealer, target)) {
            return 3 + Random.random(4);    // 3-6 base bonus weakness
        }
        return 0;
    }

    @Override public void onSkillUse(Skill skill, Combat c, Character user, Character target) {
        if (skill instanceof Tickle && canTickleNaughtyBits(c, user, target)) {
            skill.writeOutput(c, Result.special, user, target);
            skill.statusCheck(new Winded(target.getType(), 1), c, user, target, .25);
        }
    }
}
