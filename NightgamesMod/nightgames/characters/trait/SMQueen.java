package nightgames.characters.trait;

import nightgames.characters.Character;
import nightgames.combat.Combat;

/**
 * Stance dominance willpower losses occur every turn instead of the default two.
 */
public class SMQueen extends BaseTrait {
    SMQueen() {
        super("SM Queen", "A natural dom.");
    }

    @Override public int dominanceTimer(Combat c, Character self, Character other) {
        // ticks every turn instead of every two turns
        return 1;
    }

    @Override public String dominanceFormat(Combat c, Character self, Character other) {
        return "{self:NAME-POSSESSIVE} cold gaze in {self:possessive} dominant position makes {other:direct-object} shiver.";
    }
}
