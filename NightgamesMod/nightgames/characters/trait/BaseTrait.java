package nightgames.characters.trait;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.requirements.Requirement;
import nightgames.skills.Skill;
import nightgames.status.Status;

import java.util.Collections;
import java.util.List;

/**
 * Base class for traits.
 */
public abstract class BaseTrait {
    final String name;
    final String description;

    BaseTrait(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Requirements that need to be satisfied in order for this trait to appear during feat selection on level-up.
     * @return A list of requirements, all of which must be met.
     */
    List<Requirement> getRequirements() {
        // Traits with unspecified requirements will never appear during trait selection level-up.
        return Collections.singletonList((c, self, other) -> false);
    }

    public String describe(Character self) {
        // Traits with unspecified description methods will not modify character descriptions.
        return "";
    }

    public void onCausePain(Combat c, Character source, Character target) {}

    /**
     * When NPCs choose which skill to use each round, higher weights make a skill more likely to be chosen.
     * Weight modifications from traits stack additively.
     *
     * @param skill The skill currently under consideration.
     * @param c The current combat.
     * @param self The character deciding on a skill
     * @return How much to increase the weight of the skill.
     */
    public double skillWeightMod(Skill skill, Combat c, Character self) {
        return 0;
    }

    public void endOfTurn(Combat c, Character self, Character opponent) {}

    public void onSkillUse(Skill skill, Combat c, Character user, Character target) {}

    /**
     * The effect the trait has on the chance for a skill to apply a status. The result is an additive multiplier to the base
     * apply chance.
     *
     * A multiplier of .20 on a base application chance of 25% would result in a 30% application chance. A multiplier
     * of 1.00 on a base application chance of 25% would result in a 50% application chance.
     *
     * @param skill
     * @param c
     * @param user
     * @param target
     * @param possible
     * @return
     */
    public double statusChanceMultiplier(Skill skill, Combat c, Character user, Character target, Status possible) {
        return 0;
    }

    public int receiveTemptBonusDamage(Combat c, Character self, Character tempter, BodyPart with, int incomingDamage,
                    Skill skill) {
        return 0;
    }

    public int dealTemptBonusDamage(Combat c, Character tempter, Character target, BodyPart with, int baseDamage,
                    Skill skill) {
        return 0;
    }
}
