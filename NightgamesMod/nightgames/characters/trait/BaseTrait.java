package nightgames.characters.trait;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.requirements.Requirement;
import nightgames.skills.Skill;

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

    public abstract String describe(Character self);

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
}
